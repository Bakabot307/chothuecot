package com.shopMe.quangcao.orderDetail;

import com.shopMe.quangcao.order.OrderStatus;
import com.shopMe.quangcao.common.Helper;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailService {

  @Autowired
  private OrderDetailRepository orderDetailRepository;

  public List<OrderDetail> getOrderDetailByOrderId(Integer orderId) {
    return orderDetailRepository.findByOrderId(orderId);
  }

  public List<OrderDetail> getOrderDetailByUser(Integer id, String status) {
    List<OrderDetail> orderDetailList = orderDetailRepository.findByUserId(id);
    Date today = new Date();
    if (status.equals("expired")) {
      orderDetailList = orderDetailList.stream()
          .filter(orderDetail -> orderDetail.getOrders().getStatus() != OrderStatus.USER_CONFIRMED
              && orderDetail.getOrders().getStatus() != OrderStatus.NEW
              && orderDetail.getOrders().getStatus() != OrderStatus.EXTEND
              && orderDetail.getOrders().getStatus() != OrderStatus.CANCELLED)
          .filter(orderDetail -> orderDetail.getExpiredDate() != null)
          .filter(orderDetail -> orderDetail.getExpiredDate().before(today))

          .sorted(Comparator.comparing(OrderDetail::getExpiredDate))
          .filter(Helper.distinctByKey(orderDetail -> orderDetail.getProduct().getId())).collect(
              Collectors.toList());
        
    } else if (status.equals("hiring")) {
      orderDetailList = orderDetailList.stream()
          .filter(orderDetail -> orderDetail.getOrders().getStatus() == OrderStatus.PAID)
          .filter(orderDetail -> orderDetail.getExpiredDate() != null)
          .filter(orderDetail -> orderDetail.getExpiredDate().after(today))
          .sorted(Comparator.comparing(OrderDetail::getExpiredDate).reversed())
          .filter(Helper.distinctByKey(orderDetail -> orderDetail.getProduct().getId())).collect(
              Collectors.toList());
    }
    return orderDetailList;
  }


  public List<OrderDetail> getOrderDetailBeingOrdered() {
    List<OrderDetail> orderDetailList = orderDetailRepository.findAllBeingOrdered(new Date());
    Date today = new Date();
    orderDetailList = orderDetailList.stream()
        .filter(orderDetail -> orderDetail.getExpiredDate() != null)
        .filter(orderDetail -> orderDetail.getExpiredDate().after(today))
        .sorted(Comparator.comparing(OrderDetail::getExpiredDate).reversed())
        .filter(Helper.distinctByKey(orderDetail -> orderDetail.getProduct().getId())).collect(
            Collectors.toList());
    return orderDetailList;
  }
}
