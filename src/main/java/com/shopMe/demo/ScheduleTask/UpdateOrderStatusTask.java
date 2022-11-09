package com.shopMe.demo.ScheduleTask;

import com.shopMe.demo.Order.OrderRepository;
import com.shopMe.demo.Order.OrderService;
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
