package com.shopMe.quangcao.order.dto;

import com.shopMe.quangcao.order.OrderStatus;
import java.util.Date;

public class OrderResponseDto {

  private Integer id;

  private Date orderTime;


  private int ProductAvailable;

  private int TotalProduct;

  private OrderStatus status;


  public OrderResponseDto() {
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


  public int getProductAvailable() {
    return ProductAvailable;
  }

  public void setProductAvailable(int productAvailable) {
    ProductAvailable = productAvailable;
  }

  public int getTotalProduct() {
    return TotalProduct;
  }

  public void setTotalProduct(int totalProduct) {
    TotalProduct = totalProduct;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }


}
