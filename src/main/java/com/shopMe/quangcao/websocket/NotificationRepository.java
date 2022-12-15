package com.shopMe.quangcao.websocket;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {


  @Query("SELECT n FROM Notification n WHERE n.checked= ?1 AND n.userId = ?2")
  List<Notification> findByChecked(boolean b, Integer userId);

  List<Notification> findByUserId(Integer userId);

  @Query("SELECT n FROM Notification n WHERE n.userId is null AND n.checked= ?1")
  List<Notification> findByCheckedForAdmin(boolean b);
}
