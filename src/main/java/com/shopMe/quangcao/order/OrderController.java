package com.shopMe.quangcao.order;


import com.shopMe.quangcao.cartItem.CartItem;
import com.shopMe.quangcao.cartItem.CartItemService;
import com.shopMe.quangcao.order.dto.OrderAdminDto;
import com.shopMe.quangcao.order.dto.OrderResponseDto;
import com.shopMe.quangcao.order.dto.Reorder;
import com.shopMe.quangcao.orderDetail.OrderDetail;
import com.shopMe.quangcao.orderDetail.OrderDetailService;
import com.shopMe.quangcao.product.ProductStatus;
import com.shopMe.quangcao.scheduleTask.ScheduledTasks;
import com.shopMe.quangcao.scheduleTask.UpdateOrderStatusTask;
import com.shopMe.quangcao.settings.EmailSettingBag;
import com.shopMe.quangcao.settings.SettingService;
import com.shopMe.quangcao.common.Utility;
import com.shopMe.quangcao.common.ApiResponse;
import com.shopMe.quangcao.common.Helper;
import com.shopMe.quangcao.exceptions.OrderCantExtendException;
import com.shopMe.quangcao.exceptions.OrderNotFoundException;
import com.shopMe.quangcao.user.User;
import com.shopMe.quangcao.user.UserRepository;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class OrderController {


  CartItemService cartItemService;

  OrderService orderService;


  OrderDetailService orderDetailService;


  SettingService settingService;


  private final ScheduledTasks schedule;


  private ScheduledFuture scheduledFuture;

  private SimpMessagingTemplate simpMessagingTemplate;

  private UserRepository userRepository;
  // Tasks
  @Value("${site.url}")
  private String siteURL;

  @Autowired
  public OrderController(CartItemService cartItemService, OrderService orderService,
      OrderDetailService orderDetailService, SettingService settingService, ScheduledTasks schedule,
      ScheduledAnnotationBeanPostProcessor postProcessor,
      UpdateOrderStatusTask task1, SimpMessagingTemplate simpMessagingTemplate,UserRepository userRepository) {
    this.cartItemService = cartItemService;
    this.orderService = orderService;
    this.orderDetailService = orderDetailService;
    this.settingService = settingService;
    this.schedule = schedule;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.userRepository = userRepository;
  }

  @RolesAllowed("ROLE_USER")
  @PostMapping("/place_order")
  public ResponseEntity<ApiResponse> createOrder()
      throws MessagingException, UnsupportedEncodingException {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<CartItem> cartItems = cartItemService.getCartByUserHasProductAvailable(user);
    List<String> listName = new ArrayList<>();

    for (CartItem c : cartItems) {
      if (c.getProduct().getStatus() == ProductStatus.HIRING) {
        listName.add(c.getProduct().getName());
      }
    }
    if (!listName.isEmpty()) {
      return new ResponseEntity<>(
          new ApiResponse(false,
              "Sản phẩm đang được cho thuê, vui lòng xóa khỏi giỏ hàng" + listName),
          HttpStatus.BAD_REQUEST);
    }

    if (cartItems.isEmpty()) {
      return new ResponseEntity<>(new ApiResponse(false, "Giỏ hàng trống vui lòng thêm sản phẩm"),
          HttpStatus.BAD_REQUEST);
    } else {
      Order order = orderService.createOrder(user, cartItems);
      cartItemService.deleteByUser(user);
      sendEmailToAdmin(order);
      return new ResponseEntity<>(
          new ApiResponse(true, "Đơn hàng [" + order.getId() + "] đã đặt thành công"),
          HttpStatus.OK);
    }
  }


  @RolesAllowed("ROLE_USER")
  @PostMapping("/remove_hiring_product")
  public ResponseEntity<ApiResponse> removeHiringProduct() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<CartItem> cartItems = cartItemService.getCartByUser(user);

    for (CartItem c : cartItems) {
      if (c.getProduct().getStatus() == ProductStatus.HIRING) {
        cartItemService.deleteCartItem(c);
      }
    }
    return new ResponseEntity<>(
        new ApiResponse(true, "Đã xóa sản phẩm đang cho thuê khỏi giỏ hàng"),
        HttpStatus.OK);
  }

  @RolesAllowed("ROLE_USER")
  @PostMapping("/check_cart")
  public ResponseEntity<Boolean> checkCart() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<CartItem> cartItems = cartItemService.getCartByUser(user);
    long count = cartItems.stream().map(cartItem -> cartItem.getProduct().getStatus())
        .filter(status -> status == ProductStatus.HIRING)
        .count();
    if (count > 0) {
      return new ResponseEntity<>(true, HttpStatus.OK);
    }
    return new ResponseEntity<>(false, HttpStatus.OK);
  }


  @RolesAllowed("ROLE_USER")
  @GetMapping("/user/orders")
  public ResponseEntity<List<OrderResponseDto>> getAllOrderForUser(
      @RequestParam(required = false) OrderStatus status) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<OrderResponseDto> list = orderService.getAllOrderForUser(user, status);
    return ResponseEntity.ok().body(list);
  }

  @GetMapping("/user/order/{id}")
  @RolesAllowed("ROLE_USER")
  public ResponseEntity<Order> getUserOrderDetail(@PathVariable Integer id)
      throws OrderNotFoundException {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Order list = orderService.getOrder(id, user);
    return ResponseEntity.ok().body(list);
  }

  @GetMapping("/user/order/{id}/order_track")
  @RolesAllowed("ROLE_USER")
  public ResponseEntity<List<OrderTrack>> getOrderTracks(@PathVariable Integer id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<OrderTrack> list = orderService.getOrderTracks(id, user);
    return ResponseEntity.ok().body(list);
  }

  @RolesAllowed("ROLE_USER")
  @PutMapping("/user_confirm_payment/{id}")
  public ResponseEntity<ApiResponse> userConfirmPayment(@PathVariable Integer id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    try {
      orderService.userConfirmPayment(id, user);
      return new ResponseEntity<>(new ApiResponse(true, "Đã xác nhận thanh toán thành công"),
          HttpStatus.OK);
    } catch (OrderNotFoundException e) {
      return new ResponseEntity<>(new ApiResponse(false, e.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @RolesAllowed("ROLE_USER")
  @PutMapping("/user_cancel_order/{id}")
  public ResponseEntity<ApiResponse> userCancelOrder(@PathVariable Integer id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    try {
      orderService.userCancelOrder(id, user);
      return new ResponseEntity<>(new ApiResponse(true, "Đã hủy đơn hàng thành công"),
          HttpStatus.OK);
    } catch (OrderNotFoundException e) {
      return new ResponseEntity<>(new ApiResponse(false, e.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/admin/order/user/{id}")
  public ResponseEntity<List<Order>> getUserOrderById(@PathVariable("id") Integer id) {
    List<Order> list = orderService.findByUserId(id);
    return ResponseEntity.ok().body(list);
  }

  @GetMapping("/admin/orders/")
  public ResponseEntity<?> getOrderByStatus(
      @RequestParam(required = false) String status,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam String fromDate,
      @RequestParam String toDate) throws ParseException {
    OrderStatus eStatus = null;
    if (status != null) {
      eStatus = OrderStatus.valueOf(status);
    }

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    if(format.parse(fromDate).after(format.parse(toDate))){
      return new ResponseEntity<>("Ngày bắt đầu phải nhỏ hơn", HttpStatus.BAD_REQUEST);
    }
    List<OrderAdminDto> list = orderService.findByStatus(eStatus, keyword,format.parse(fromDate),format.parse(toDate));
    return ResponseEntity.ok().body(list);
  }


  @GetMapping("/admin/order/{id}")
  public ResponseEntity<Order> getOrderById(@PathVariable Integer id)
      throws OrderNotFoundException {
    Order list = orderService.findByOrderId(id);

    return ResponseEntity.ok().body(list);
  }

  @PutMapping("/admin/order/{id}/{action}")
  public ResponseEntity<ApiResponse> confirmOrder(
      @PathVariable Integer id,
      @PathVariable boolean action
  ) throws OrderNotFoundException, MessagingException, UnsupportedEncodingException {
    Order order = orderService.findByOrderId(id);
    if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.PAID) {
      return new ResponseEntity<>(
          new ApiResponse(false, "Đơn hàng " + id + " được hủy hoặc xác nhận"),
          HttpStatus.BAD_REQUEST);
    }
    if (!action) {
      orderService.cancelled(order);
      return new ResponseEntity<>(new ApiResponse(true, "Đơn hàng " + id + " đã bị hủy"),
          HttpStatus.OK);
    }

    orderService.confirm(order);
    sendEmailForRequest(order.getUser().getEmail(), order);
    return new ResponseEntity<>(new ApiResponse(true, "Đơn hàng" + id + " đã được xác nhận"),
        HttpStatus.OK);

  }

  @RolesAllowed("ROLE_USER")
  @PutMapping("/extend_order")
  public ResponseEntity<ApiResponse> extendOrder(
      @RequestBody Reorder reorder
  ) throws OrderCantExtendException, MessagingException, UnsupportedEncodingException {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    Order order = orderService.extendOrder(reorder, user);
    if (order == null) {
      return new ResponseEntity<>(new ApiResponse(true, "Gia hạn đơn hàng không thành công"),
          HttpStatus.BAD_REQUEST);
    } else {
      sendEmailToAdmin(order);
      return new ResponseEntity<>(
          new ApiResponse(true, "Đơn hàng gia hạn" + order.getId() + " đã được tạo thành công"),
          HttpStatus.OK);
    }
  }

  private void sendEmailToAdmin(Order order)
      throws MessagingException, UnsupportedEncodingException {
    EmailSettingBag emailSetting = settingService.getEmailSettings();
    JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSetting);
    User user = userRepository.getAllByRole("ROLE_ADMIN").stream().findAny().get();
    long totalL = (long) order.getTotal();
    String total = Long.toString(totalL);
    String subject = "You have new order " + order.getId() + " from "+siteURL;
    String content = "You have new order " + order.getId() + " from "+siteURL+
     "<p><strong>QUANTITY: " + order.getQuantity() + " </strong><br /><strong>TOTAL: "
        + total + " </strong></p>\n" +
        "<p>Click <a href="+siteURL+"/admin/orderPlace"+" style=\"background-color: #2b2301; color: #fff; display: inline-block; padding: 3px 10px; font-weight: bold; border-radius: 5px;\">here</a> to check the order!</p>";

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(emailSetting.getFromAddress(), emailSetting.getSenderName());
    helper.setTo(user.getEmail());
    helper.setSubject(subject);
    helper.setText(content, true);

    mailSender.send(message);
  }
  private void sendEmailForRequest(String email, Order order)
      throws MessagingException, UnsupportedEncodingException {
    EmailSettingBag emailSetting = settingService.getEmailSettings();
    JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSetting);
    List<OrderDetail> orderDetailList = orderDetailService.getOrderDetailByOrderId(order.getId());

    String subject = "You have been ordered " + order.getId() + " "+siteURL;

    String content1 = "<h1 style=\"color: #5e9ca0;\">Order <span style=\"color: #2b2301;\">"
        + order.getId() + "</span> has been requested!</h1>\n" +
        "<p>Please wait for the admin to confirm your order\n" +
        "<h2 style=\"color: #2e6c80;\">Your order:</h2>";
    StringBuilder content2 = new StringBuilder();
    for (OrderDetail o : orderDetailList) {
      content2.append("<tr>\n")
          .append("<td style=\"text-align:center;border:1px solid black;padding: 10px;\">")
          .append(o.getProduct().getName()).append("</td>\n")
          .append("<td style=\"text-align:center;border:1px solid black;padding: 10px;\">")
          .append(o.getProduct().getPrice())
          .append("</td>\n")
          .append("<td style=\"text-align:center;border:1px solid black;padding: 10px;\">")
          .append(o.getMonth())
          .append("</td>\n")
          .append("<td style=\"text-align:center;border:1px solid black;padding: 10px;\">")
          .append(o.getProduct().getAddress().getFullAddress())
          .append("</td>\n")
          .append("</tr>");
    }
    String content3 = "<table style=\"border:1px solid black;border-collapse:collapse;\">\n" +
        "<thead>\n" +
        "<tr>\n" +
        "<td style=\"border:1px solid black;padding: 10px;font-weight: bold;\">Name</td>\n" +
        "<td style=\"border:1px solid black;padding: 10px;font-weight: bold;\">Price</td>\n" +
        "<td style=\"border:1px solid black;padding: 10px;font-weight: bold;\">Month</td>\n" +
        "<td style=\"border:1px solid black;padding: 10px;font-weight: bold;\">Address</td>\n" +
        "</tr>\n" +
        "</thead>\n" +
        "<tbody>\n" +
        content2
        +
        "</tbody>\n" +
        "</table>" +
        "<p><strong>&nbsp;</strong></p>\n" +
        "<p><strong>QUANTITY: " + order.getQuantity() + " </strong><br /><strong>TOTAL: "
        + order.getTotal() + " </strong></p>\n" +
        "<p>Click <a href="+siteURL+"auth/order/"+order.getId()+" style=\"background-color: #2b2301; color: #fff; display: inline-block; padding: 3px 10px; font-weight: bold; border-radius: 5px;\">here</a> to check the order!</p>"+

    "<p><strong>Enjoy!</strong></p>\n" +
        "<p><strong>&nbsp;</strong></p>";

    String content = content1 + content3;
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(emailSetting.getFromAddress(), emailSetting.getSenderName());
    helper.setTo(email);
    helper.setSubject(subject);
    helper.setText(content, true);
    mailSender.send(message);
  }

  @Async
  @GetMapping(value = "/start_cool_down")
  public void executeTask(@RequestParam Integer orderId) {
    ScheduledExecutorService scheduler =
        Executors.newScheduledThreadPool(1);
    ///cancel order
    final Runnable cancelOrder = () -> {
      Order order = orderService.getByOrderId(orderId);
      if (order.getCancelTime().before(new Date())) {
        orderService.cancelled(order);
      }
    };
    final ScheduledFuture<?> cancelHandler =
        scheduler.scheduleAtFixedRate(cancelOrder, 60*60, 60 * 60, TimeUnit.SECONDS);
    scheduler.schedule(() -> {
      cancelHandler.cancel(true);
    }, 60*61, TimeUnit.SECONDS);
  }

//  @Async
//  @GetMapping(value = "/start_cool_down")
//  public void executeTask2(@RequestParam Integer orderId) {
//    System.out.println("start");
//    ScheduledExecutorService scheduler =
//        Executors.newScheduledThreadPool(1);
//    ///cancel order
//    final Runnable cancelOrder = () -> {
//      Order order = orderService.getByOrderId(orderId);
//      if (order.getCancelTime().before(new Date())) {
//        orderService.cancelled(order);
//      }
//    };
//    final ScheduledFuture<?> cancelHandler =
//        scheduler.scheduleAtFixedRate(cancelOrder, 10, 10, TimeUnit.SECONDS);
//    scheduler.schedule(() -> {
//      cancelHandler.cancel(true);
//      System.out.println("end");
//    }, 11, TimeUnit.SECONDS);
//  }

  @PostMapping("/extend_order_payment_time/{id}")
  public void extendOrderPayTime(@RequestParam(required = false) String day,
      @PathVariable Integer id)
      throws OrderNotFoundException {
    Order order = orderService.findByOrderId(id);
    Date date = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    Date today = cal.getTime();
    order.setCancelTime(today);
    if (Objects.equals(day, "tomorrow")) {
      order.setCancelTime(Helper.PlusDay(today, 1));
    }
    orderService.save(order);
  }
}