package com.shopMe.quangcao.order.dto;

import com.shopMe.quangcao.order.Order;
import com.shopMe.quangcao.order.OrderStatus;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


public class OrderAdminDto {

  private Integer id;

  private Date orderTime;

  private Integer quantity;

  private float total;

  private String orderCode;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  private String phoneNumber;

  private String fullName;


  public OrderAdminDto() {
  }


  public OrderAdminDto(Order order) {
    this.id = order.getId();
    this.orderTime = order.getOrderTime();
    this.quantity = order.getQuantity();
    this.total = order.getTotal();
    this.orderCode = order.getOrderCode();
    this.status = order.getStatus();
    this.fullName = order.getUser().getFullName();
    this.phoneNumber = order.getUser().getPhoneNumber();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Date getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(Date orderTime) {
    this.orderTime = orderTime;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public float getTotal() {
    return total;
  }

  public void setTotal(float total) {
    this.total = total;
  }

  public String getOrderCode() {
    return orderCode;
  }

  public void setOrderCode(String orderCode) {
    this.orderCode = orderCode;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
  

}
