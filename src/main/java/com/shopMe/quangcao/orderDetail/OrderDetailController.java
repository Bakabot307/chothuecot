package com.shopMe.quangcao.orderDetail;

import com.shopMe.quangcao.user.User;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class OrderDetailController {

  @Autowired
  private OrderDetailService orderDetailService;

  @GetMapping("/order/{orderId}")
  public ResponseEntity<List<OrderDetail>> getByOrderId(
      @PathVariable("orderId") Integer orderId
  ) {
    List<OrderDetail> list = orderDetailService.getOrderDetailByOrderId(orderId);
    return new ResponseEntity<>(list, HttpStatus.OK);
  }

  @RolesAllowed("ROLE_USER")
  @GetMapping("user/orderdetail")
  public ResponseEntity<List<OrderDetail>> getByOrderId(
      @RequestParam("status") String status
  ) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<OrderDetail> list = orderDetailService.getOrderDetailByUser(user.getId(), status);
    return new ResponseEntity<>(list, HttpStatus.OK);
  }
}
