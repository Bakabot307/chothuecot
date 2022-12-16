package com.shopMe.quangcao.scheduleTask;

import com.shopMe.quangcao.order.OrderRepository;
import com.shopMe.quangcao.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateOrderStatusTask {

  @Autowired
  OrderRepository orderRepository;
  @Autowired
  OrderService orderService;

  public Runnable test(Integer id) {
    Runnable aRunnable = () -> {
      System.out.println("test" + id);
    };
    return aRunnable;
  }
}
