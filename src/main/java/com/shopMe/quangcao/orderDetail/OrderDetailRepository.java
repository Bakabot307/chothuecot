package com.shopMe.quangcao.orderDetail;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

  @Query("SELECT o FROM OrderDetail o WHERE o.orders.id = ?1")
  List<OrderDetail> findByOrderId(Integer orderId);

  @Query(
      "SELECT o from OrderDetail o where o.orders.user.id <> ?1 AND o.expiredDate > ?2 "
          + "GROUP BY o.product.id ")
  List<OrderDetail> findByProductStatusAndNotUserId(int id, Date date);


  @Query("SELECT o FROM OrderDetail o WHERE o.orders.user.id = ?1")
  List<OrderDetail> findByUserId(Integer id);

  @Query("SELECT o FROM OrderDetail o WHERE o.expiredDate IS NOT NULL AND o.expiredDate > ?1")
  List<OrderDetail> findAllBeingOrdered(Date date);

  @Query("SELECT o FROM OrderDetail o WHERE o.startDate IS NOT NULL "
      + "AND o.startDate > ?1 AND o.startDate < ?2")
  List<OrderDetail> findAllByDate(Date date1, Date date2);


  @Query("SELECT o FROM OrderDetail o WHERE o.startDate IS NOT NULL "
      + "AND o.startDate > ?1 AND o.startDate < ?2 AND o.product.address.street LIKE %?3%")
  List<OrderDetail> findAllByDate(Date date1, Date date2, String streetId);
}
