package com.shopMe.demo.Websocket;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  @Autowired
  private NotificationRepository notificationRepository;

  public List<Notification> getNotifications(Integer userId) {


    if (userId == null) {
      return notificationRepository.findByUserId(null).stream()
          .sorted(Comparator.comparing(Notification::getDate).reversed())
          .collect(Collectors.toList());
    } else {
      return notificationRepository.findByUserId(userId).stream()
          .sorted(Comparator.comparing(Notification::getDate).reversed())
          .collect(Collectors.toList());
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
