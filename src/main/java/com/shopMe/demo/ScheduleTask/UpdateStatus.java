package com.shopMe.demo.ScheduleTask;

import com.shopMe.demo.Order.OrderRepository;
import com.shopMe.demo.OrderDetail.OrderDetail;
import com.shopMe.demo.OrderDetail.OrderDetailRepository;
import com.shopMe.demo.OrderDetail.OrderDetailService;
import com.shopMe.demo.Product.ProductRepository;
import com.shopMe.demo.Settings.EmailSettingBag;
import com.shopMe.demo.Settings.SettingService;
import com.shopMe.demo.Utility;
import com.shopMe.demo.Websocket.NotificationService;
import com.shopMe.demo.config.Helper;
import com.shopMe.demo.user.User;
import com.shopMe.demo.user.UserNotFoundException;
import com.shopMe.demo.user.UserService;
import com.shopMe.demo.wishList.WishList;
import com.shopMe.demo.wishList.WishListService;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class UpdateStatus {

  ProductRepository productRepository;

  OrderDetailRepository orderDetailRepository;

  OrderRepository orderRepository;

  SettingService settingService;

  OrderDetailService orderDetailService;

  UserService userService;

  WishListService wishListService;

  Date today = new Date();

  NotificationService notificationService;

  @Autowired
  public UpdateStatus(ProductRepository productRepository,
      OrderDetailRepository orderDetailRepository, OrderRepository orderRepository,
      SettingService settingService, OrderDetailService orderDetailService,
      UserService userService, WishListService wishListService) {
    this.productRepository = productRepository;
    this.orderDetailRepository = orderDetailRepository;
    this.orderRepository = orderRepository;
    this.settingService = settingService;
    this.orderDetailService = orderDetailService;
    this.userService = userService;
    this.wishListService = wishListService;
  }

  public void SendEmailToUserThatHasProductExpiring() throws UserNotFoundException {
    List<OrderDetail> orderDetailList = orderDetailService.getOrderDetailBeingOrdered();
    Map<Integer, List<OrderDetail>> result =
        orderDetailList.stream()
            .filter(o -> o.getExpiredDate().after(today)
                && o.getExpiredDate().before(Helper.PlusDay(today, 2)))
            .collect(Collectors.groupingBy(o -> o.getOrders().getUser().getId()));

    for (Map.Entry<Integer, List<OrderDetail>> entry : result.entrySet()) {
      User user = userService.findById(entry.getKey());
      sendEmail(user.getEmail(), entry.getValue());
    }

  }


  public void SendEmailToUserThatHasProductInWishlist() throws UserNotFoundException {
    List<WishList> wishListList = wishListService.getallWishList();
    Map<User, List<WishList>> result =
        wishListList.stream()
            .filter(o -> o.getProduct().getExpiredDate() == null)
            .collect(Collectors.groupingBy(WishList::getUser));
    for (Map.Entry<User, List<WishList>> entry : result.entrySet()) {
      //create product template send email
//      sendEmail(user.getEmail(), entry.getValue());
    }
  }


  private void sendEmail(String email, List<OrderDetail> orderDetailList) {
    EmailSettingBag emailSetting = settingService.getEmailSettings();
    JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSetting);

    String subject = "Trụ sắp hết hạn cho thuê";
    String content1 = "<h1 style=\"color: #5e9ca0;\">Order <span style=\"color: #2b2301;\">"
        + "Một số trụ từ đơn hàng" + "</span> sắp hết hạn cho thuê!</h1>\n" +
        "<h2 style=\"color: #2e6c80;\">Thông tin trụ:</h2>";
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
          .append(new SimpleDateFormat("dd-MM-yyyy").format(o.getExpiredDate()))
          .append("</td>\n")
          .append("</tr>");
    }
    String content3 = "<table style=\"border:1px solid black;border-collapse:collapse;\">\n" +
        "<thead>\n" +
        "<tr>\n" +
        "<td style=\"border:1px solid black;padding: 10px;font-weight: bold;\">Mã số trụ</td>\n" +
        "<td style=\"border:1px solid black;padding: 10px;font-weight: bold;\">Giá</td>\n" +
        "<td style=\"border:1px solid black;padding: 10px;font-weight: bold;\">Tháng thuê</td>\n" +
        "<td style=\"border:1px solid black;padding: 10px;font-weight: bold;\">Ngày hết hạn</td>\n"
        +
        "</tr>\n" +
        "</thead>\n" +
        "<tbody>\n" +
        content2
        +
        "</tbody>\n" +
        "</table>" +
        "<p><strong>&nbsp;</strong></p>\n" +
        "<p><strong>SỐ LƯỢNG: " + orderDetailList.size() +
        "<p>Click <a style=\"background-color: #2b2301; color: #fff; display: inline-block; padding: 3px 10px; font-weight: bold; border-radius: 5px;\">Here</a> để kiểm tra đơn hàng nếu khách hàng muốn gia hạn cho thuê!</p>\n"
        +
        "<p><strong>Thanks!</strong></p>\n" +
        "<p><strong>&nbsp;</strong></p>";

    String content = content1 + content3;
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);
    try {
      helper.setFrom(emailSetting.getFromAddress(), emailSetting.getSenderName());
      helper.setTo(email);
      helper.setSubject(subject);
      helper.setText(content, true);
    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    mailSender.send(message);
  }
}