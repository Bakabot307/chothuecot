package com.shopMe.quangcao.websocket;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/notification")
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;

  @GetMapping("/")
  public List<Notification> getNotifications(
      @RequestParam(value = "id", required = false) Integer userId) {
    return notificationService.getNotifications(userId);
  }

  @PostMapping("/markAsRead/")
  public void markAsRead(@RequestParam(required = false) String userId) {
    notificationService.markAsRead(userId == null ? null : Integer.parseInt(userId));
  }

  int count = 0;


  @PostMapping("/add_and_test_notification_user/{id}")
  public void testUser(@PathVariable("id") String id, @RequestBody Notification notification) {
    notification.setUserId(Integer.parseInt(id));
    notification.setDate(new Date());
    notification.setUserId(Integer.parseInt(id));
    simpMessagingTemplate.convertAndSendToUser(id, "/private",
        notificationService.addNotification2(notification));
  }


  @PostMapping("/add_and_test_notification_admin")
  public void testUser(@RequestBody Notification notification) {
    notificationService.addNotification(notification);
    simpMessagingTemplate.convertAndSend("/notification/public",
        new Notification("hello from server " + count, new Date(), MessageType.ORDER,
            false, null, null));


  }


  @MessageMapping("/private-message")
  public Notification recMessage(@Payload Notification message) {
    simpMessagingTemplate.convertAndSendToUser(message.getUserId().toString(), "/user", message);
    simpMessagingTemplate.convertAndSendToUser("15", "/user",
        new Notification("yo", new Date(), MessageType.ORDER, false, null, message.getUserId()));
    return message;
  }

  @MessageMapping("/private-message/cart")
  public Notification cartUpdate(@Payload Notification message) {
    simpMessagingTemplate.convertAndSendToUser(message.getUserId().toString(), "/user", message);
    simpMessagingTemplate.convertAndSendToUser("15", "/user",
        "cart");
    return message;
  }
}
