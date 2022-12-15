package com.shopMe.quangcao.cartItem.dto;

public class CartItemDto {

  private Integer id;


  private Integer productId;

  private Integer month;

  public CartItemDto() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public Integer getProductId() {
    return productId;
  }

  public void setProductId(Integer productId) {
    this.productId = productId;
  }

  public Integer getMonth() {
    return month;
  }

  public void setMonth(Integer month) {
    this.month = month;
  }
}
