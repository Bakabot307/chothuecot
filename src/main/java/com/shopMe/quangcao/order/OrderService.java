package com.shopMe.quangcao.order;

import com.shopMe.quangcao.cartItem.CartItem;
import com.shopMe.quangcao.cartItem.CartItemRepository;
import com.shopMe.quangcao.order.dto.OrderAdminDto;
import com.shopMe.quangcao.order.dto.OrderResponseDto;
import com.shopMe.quangcao.order.dto.Reorder;
import com.shopMe.quangcao.orderDetail.OrderDetail;
import com.shopMe.quangcao.orderDetail.OrderDetailRepository;
import com.shopMe.quangcao.orderDetail.OrderDetailService;
import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.product.ProductRepository;
import com.shopMe.quangcao.product.ProductStatus;
import com.shopMe.quangcao.websocket.MessageType;
import com.shopMe.quangcao.websocket.Notification;
import com.shopMe.quangcao.websocket.NotificationService;
import com.shopMe.quangcao.common.Helper;
import com.shopMe.quangcao.exceptions.OrderCantExtendException;
import com.shopMe.quangcao.exceptions.OrderNotFoundException;
import com.shopMe.quangcao.user.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderService {


  private final OrderRepository orderRepository;


  private final OrderDetailRepository orderDetailRepository;


  private final ProductRepository productRepository;





  private final CartItemRepository cartItemRepository;

  private final OrderDetailService orderDetailService;

  private SimpMessagingTemplate simpMessagingTemplate;

  private final NotificationService notificationService;

  @Autowired
  public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository,
      ProductRepository productRepository,
      CartItemRepository cartItemRepository, OrderDetailService orderDetailService,
      SimpMessagingTemplate simpMessagingTemplate, NotificationService notificationService) {
    this.orderRepository = orderRepository;
    this.orderDetailRepository = orderDetailRepository;
    this.productRepository = productRepository;
    this.cartItemRepository = cartItemRepository;
    this.orderDetailService = orderDetailService;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.notificationService = notificationService;

  }

  public Order createOrder(User user, List<CartItem> cartItems) {

    Order order = new Order();
    Date today = new Date();

    order.setOrderTime(today);
    order.setUser(user);
    order.setQuantity(cartItems.size());

    order.setOrderCode(generateOrderCode(user.getId(), "O"));
    Set<OrderDetail> orderDetails = order.getOrderDetail();
    for (CartItem cartItem : cartItems) {
      Product product = cartItem.getProduct();
      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setOrders(order);
      orderDetail.setProduct(product);
      orderDetail.setMonth(cartItem.getMonth());
      orderDetail.setExpiredDate(Helper.PlusMonth(today, cartItem.getMonth()));
      orderDetail.setStartDate(today);
      orderDetails.add(orderDetail);
    }

    order.setTotal(calculateProductCost(orderDetails));
    OrderTrack track = new OrderTrack();
    track.setOrder(order);
    track.setStatus(OrderStatus.NEW);
    track.setNotes(OrderStatus.NEW.defaultDescription());
    track.setUpdatedTime(new Date());

    order.getOrderTracks().add(track);
    order.setCancelTime(Helper.PlusHour(today, 1));
    Order orderSaved = orderRepository.save(order);
    Notification notification = new Notification("Đơn hàng mã số " + orderSaved.getOrderCode() + " mới được đặt",
        new Date(), MessageType.ORDER,
        false, orderSaved.getId(), null);
    Notification notification2 = notificationService.addNotification2(notification);
    simpMessagingTemplate.convertAndSend("/notification/public", notification2);

    return order;
  }


  public Order getOrder(Integer id, User user) throws OrderNotFoundException {
    Order order = orderRepository.findByIdAndUser(user.getId(), id);
    if (Objects.isNull(order)) {
      throw new OrderNotFoundException("Order not found");
    }

    return order;
  }

  public List<Order> findByUserId(Integer id) {
    return orderRepository.findByUserId(id);
  }

  public List<OrderResponseDto> getAllOrderForUser(User user, OrderStatus status) {
    List<Order> list = orderRepository.findByUserAndAndHasNoParentOrder(user.getId())
        .stream()
        .filter(order -> {
          if (status == null) {
            return true;
          }
          return order.getStatus() == status;
        })
        .sorted(Comparator.comparing(Order::getConfirmedTime, Comparator.nullsFirst(
            Comparator.naturalOrder())).reversed().thenComparing(Order::getStatus)).toList();

    List<OrderResponseDto> listOrderDto = new ArrayList<>();

    for (Order order : list) {
      OrderResponseDto orderDto = new OrderResponseDto();
      orderDto.setId(order.getId());
      orderDto.setOrderTime(order.getOrderTime());
      orderDto.setTotalProduct(order.getQuantity());
      orderDto.setStatus(order.getStatus());

      int productAvailable = (int) order.getOrderDetail().stream()
          .map(o -> o.getProduct().getStatus() == ProductStatus.AVAILABLE)
          .count();

      orderDto.setProductAvailable(productAvailable);

      listOrderDto.add(orderDto);
    }
    return listOrderDto;
  }

  public List<OrderAdminDto> findByStatus(OrderStatus status, String keyword,Date fromDate, Date toDate) {
    List<OrderAdminDto> list = new ArrayList<>();
    List<Order> orders;
    if(Objects.equals(keyword, "")){
      keyword = null;
    }
    if (status == null && keyword == null ) {
      orders = orderRepository.findAll().stream()
          .filter(order -> order.getOrderTime().after(fromDate) && order.getOrderTime().before(toDate))
          .sorted(Comparator.comparing(Order::getId, Comparator.nullsLast(
              Comparator.naturalOrder())).reversed())
          .collect(Collectors.toList());

    } else if (status != null && keyword != null) {
      orders = orderRepository.findByStatusAndKeyword(keyword).stream()
          .filter(o -> o.getStatus() == status)
          .filter(order -> order.getOrderTime().after(fromDate) && order.getOrderTime().before(toDate))
          .sorted(Comparator.comparing(Order::getId, Comparator.nullsLast(
              Comparator.naturalOrder())).reversed())
          .collect(Collectors.toList());
    } else if (status != null) {
      orders = orderRepository.findAll().stream()
          .filter(Objects::nonNull)
          .filter(o -> o.getStatus() == status)
          .filter(order -> order.getOrderTime().after(fromDate) && order.getOrderTime().before(toDate))
          .sorted(Comparator.comparing(Order::getId, Comparator.nullsLast(
              Comparator.naturalOrder())).reversed())
          .collect(Collectors.toList());
    } else {
      orders = orderRepository.findByStatusAndKeyword(keyword)
          .stream()
          .filter(order -> order.getOrderTime().after(fromDate) && order.getOrderTime().before(toDate))
          .sorted(Comparator.comparing(Order::getId, Comparator.nullsLast(
              Comparator.naturalOrder())).reversed()).toList();
    }
    for (Order order : orders) {
      OrderAdminDto orderAdminDto = new OrderAdminDto(order);
      list.add(orderAdminDto);
    }
    return list;
  }


  private String generateOrderCode(Integer id, String regex) {
    int i = (int) (new Date().getTime() / 1000);

    return regex + id + i;

  }

  private float calculateProductCost(Set<OrderDetail> orderDetail) {
    float cost = 0.0f;

    for (OrderDetail item : orderDetail) {
      cost += item.getMonth() * item.getProduct().getPrice();
    }
    return cost;
  }

  public Order findByOrderId(Integer id) throws OrderNotFoundException {
    Optional<Order> order = orderRepository.findById(id);
    if (order.isEmpty()) {
      throw new OrderNotFoundException("Đơn hàng không tồn tại");
    }
    return order.get();
  }


  public Order getByOrderId(Integer id) {
    return orderRepository.findById(id).get();
  }

  public void confirm(Order order) {

    Date confirmDate = new Date();
    order.setConfirmedTime(confirmDate);
    if(order.getOrderType() == OrderStatus.NEW){
      List<OrderDetail> listDetail = orderDetailRepository.findByOrderId(order.getId());
      for (OrderDetail orderDetail : listDetail) {
        orderDetail.setExpiredDate(Helper.PlusMonth(confirmDate, orderDetail.getMonth()));
        orderDetail.setStartDate(confirmDate);
      }
    }


    OrderTrack track = new OrderTrack();
    track.setOrder(order);
    track.setStatus(OrderStatus.PAID);
    track.setNotes(OrderStatus.PAID.defaultDescription());
    track.setUpdatedTime(new Date());
    order.setCancelTime(null);
    order.getOrderTracks().add(track);

    Notification notification = new Notification(
        "Đơn hàng mã số " + order.getOrderCode() + " của bạn đã được xác nhận",
        new Date(), MessageType.ORDER,
        false, order.getId(), order.getUser().getId());
    Notification notification2 = notificationService.addNotification2(notification);
    simpMessagingTemplate.convertAndSend("/user/" + order.getUser().getId() + "/private",
        notification2);

    orderRepository.save(order);

  }


  public void cancelled(Order order) {
    Date confirmDate = new Date();
    order.setConfirmedTime(confirmDate);
    order.setCancelTime(null);
    OrderTrack track = new OrderTrack();

    for (OrderDetail orderDetail : order.getOrderDetail()) {
      orderDetail.setExpiredDate(null);
      orderDetail.setStartDate(null);
    }
    track.setOrder(order);
    track.setStatus(OrderStatus.CANCELLED);
    track.setNotes(OrderStatus.CANCELLED.defaultDescription());
    track.setUpdatedTime(new Date());
    order.getOrderTracks().add(track);

    Order orderSaved = orderRepository.save(order);

    Notification notification = new Notification(
        "Đơn hàng mã số " + orderSaved.getOrderCode() + " của bạn đã bị hủy",
        new Date(), MessageType.ORDER,
        false, order.getId(), order.getUser().getId());
    Notification notification2 = notificationService.addNotification2(notification);
    simpMessagingTemplate.convertAndSend("/user/" + order.getUser().getId() + "/private",
        notification2
    );

  }

  public void save(Order order) {
    orderRepository.save(order);
  }

  public Order extendOrder(Reorder reorder, User user) throws OrderCantExtendException {

    List<OrderDetail> listOrderDB = orderDetailService.getOrderDetailByUser(user.getId(), "extend");
    Date today = new Date();
    Set<OrderDetail> listOrderDetails = new HashSet<>();
    Order newOrder = new Order();

    List<Order> listOrder = findByUserId(user.getId());
    for (Order order : listOrder) {
      if (order.getOrderType().equals(OrderStatus.EXTEND)
          && order.getStatus().equals(OrderStatus.EXTEND)
          || order.getOrderType().equals(OrderStatus.EXTEND) && order.getStatus().equals(OrderStatus.USER_CONFIRMED)) {
        throw new OrderCantExtendException(
            "Bạn đang có đơn hàng đang gia hạn chưa hoàn thành, vui lòng kiểm tra lại");
      }
    }
    float cost = 0.0f;
    for (Map.Entry<Integer, Integer> pair : reorder.getProductInfo().entrySet()) {
      Product product = productRepository.findById(pair.getKey()).get();

      if (!contains(listOrderDB, product.getId())) {
        throw new OrderCantExtendException("Trụ " + product.getName() + " không thể gia hạn");
      }
      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setOrders(newOrder);
      orderDetail.setMonth(pair.getValue());
      orderDetail.setStartDate(product.getExpiredDate());
      orderDetail.setProduct(product);
      orderDetail.setExpiredDate(Helper.PlusMonth(product.getExpiredDate(), pair.getValue()));
      listOrderDetails.add(orderDetail);

      cost += pair.getValue() * product.getPrice();

    }
    newOrder.setOrderDetail(listOrderDetails);
    OrderTrack track = new OrderTrack();
    track.setOrder(newOrder);
    track.setStatus(OrderStatus.EXTEND);
    track.setNotes(OrderStatus.EXTEND.defaultDescription());
    track.setUpdatedTime(today);

    newOrder.setUser(user);
    newOrder.setOrderTime(today);
    newOrder.setQuantity(reorder.getProductInfo().size());
    newOrder.setTotal(cost);
    newOrder.setOrderCode(generateOrderCode(user.getId(), "E"));
    newOrder.getOrderTracks().add(track);
    newOrder.setCancelTime(Helper.PlusHour(today, 1));
    Order orderSaved = orderRepository.save(newOrder);

    Notification notification = new Notification(
        "Đơn hàng gia hạn mã số " + orderSaved.getOrderCode() + " vừa được đặt",
        new Date(), MessageType.ORDER,
        false, orderSaved.getId(), null);
    Notification notification2 = notificationService.addNotification2(notification);
    simpMessagingTemplate.convertAndSend("/notification/public", notification2);

    return orderSaved;
  }


  public List<OrderTrack> getOrderTracks(Integer id, User user) {
    Order order = orderRepository.findById(id).get();
    return orderRepository.getChildOrderTracks(user.getId(), id);
  }

  public boolean contains(final List<OrderDetail> list, final Integer id) {
    return list.stream().anyMatch(o -> o.getProduct().getId().equals(id));
  }

  public void deleteOrder(Integer id) throws OrderNotFoundException {
    Order order = findByOrderId(id);
    if (order == null) {
      throw new OrderNotFoundException("Đơn hàng không tồn tại hoặc đã được xóa");
    }
    orderRepository.deleteById(id);
  }


  public void userConfirmPayment(Integer id, User user) throws OrderNotFoundException {
    Order order = getOrder(id, user);
    if(order.getStatus().equals(OrderStatus.EXTEND) || order.getStatus().equals(OrderStatus.NEW)){
      OrderTrack track = new OrderTrack();
      track.setOrder(order);
      track.setStatus(OrderStatus.USER_CONFIRMED);
      track.setNotes(OrderStatus.USER_CONFIRMED.defaultDescription());
      track.setUpdatedTime(new Date());
      order.getOrderTracks().add(track);
      Order orderSaved = orderRepository.save(order);
      Notification notification = new Notification(
          "Đơn hàng mã số " + orderSaved.getOrderCode() + " đã được xác nhận",
          new Date(), MessageType.ORDER,
          false, orderSaved.getId(), null);
      Notification notification2 = notificationService.addNotification2(notification);
      simpMessagingTemplate.convertAndSend("/notification/public", notification2);
    } else {
      throw new OrderNotFoundException("Đơn hàng không tồn tại hoặc đã được xử lý");
    }


  }

  public void userCancelOrder(Integer id, User user) throws OrderNotFoundException {
    Order order = getOrder(id, user);
    OrderTrack track = new OrderTrack();
    track.setOrder(order);
    track.setStatus(OrderStatus.USER_CANCELLED);
    track.setNotes(OrderStatus.USER_CANCELLED.defaultDescription());
    track.setUpdatedTime(new Date());
    order.getOrderTracks().add(track);
    Order orderSaved = orderRepository.save(order);
    Notification notification = new Notification(
        "Đơn hàng mã số " + orderSaved.getId() + " đã bị hủy",
        new Date(), MessageType.ORDER,
        false, orderSaved.getId(), null);
    Notification notification2 = notificationService.addNotification2(notification);
    simpMessagingTemplate.convertAndSend("/notification/public", notification2);
  }

}
