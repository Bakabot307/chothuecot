package com.shopMe.quangcao.order;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Integer> {


  @Query("SELECT o FROM Order o WHERE o.user.id = ?1")
  List<Order> findByUserId(Integer userId);

  @Query("SELECT o FROM Order o WHERE o.user.id = ?1  AND o.id = ?2")
  Order findByIdAndUser(Integer userId, Integer orderId);

  @Query("SELECT o FROM Order o WHERE o.user.id = ?1")
  List<Order> findByUserAndAndHasNoParentOrder(Integer userId);


  @Query("SELECT o FROM Order o WHERE  concat(o.orderCode,'') like concat('%',?1, '%') "
      + "OR o.id = ?1 "
      + "OR o.user.firstName like %?1% "
      + "OR o.user.lastName like %?1% "
      + "OR concat( o.user.firstName,' ', o.user.lastName) like concat('%',?1, '%') "
      + "OR concat(o.user.phoneNumber,'') like  concat('%',?1, '%') ")
  List<Order> findByStatusAndKeyword(String orderCode);

  @Query("SELECT o.orderTracks FROM Order o WHERE o.user.id = ?1 AND o.id = ?2")
  List<OrderTrack> getChildOrderTracks(Integer userId, Integer orderId);

  @Query("SELECT o FROM Order o WHERE o.cancelTime is not null AND o.cancelTime < ?1")
  List<Order> getOrderHaveNotPaid(Date today);

}
