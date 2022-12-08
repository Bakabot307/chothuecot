package com.shopMe.demo.Websocket;

import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  @Autowired
  private NotificationRepository notificationRepository;

  public List<Notification> getNotifications(Integer userId) {
    List<Notification> notifications = notificationRepository.findAll();
    notifications.sort(
        Comparator.comparing(Notification::getDate)
            .reversed());
    if (userId == null) {
      return notificationRepository.findByUserId(null);
    } else {
      return notificationRepository.findByUserId(userId);
    }
  }

  public void addNotification(Notification notification) {
    System.out.println("add notification: " + notification);
    notificationRepository.save(notification);
  }

  public Notification addNotification2(Notification notification) {
    System.out.println("add notification: " + notification);
    return notificationRepository.save(notification);
  }

  public void markAsRead(Integer userId) {
    if (userId == null) {
      notificationRepository.findByCheckedForAdmin(false).forEach(n -> {
        n.setChecked(true);
        notificationRepository.save(n);
      });
    } else {
      notificationRepository.findByChecked(false, userId).forEach(n -> {
        n.setChecked(true);
        notificationRepository.save(n);
      });
    }
  }
}
