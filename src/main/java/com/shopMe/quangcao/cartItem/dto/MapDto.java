package com.shopMe.quangcao.cartItem.dto;

import java.util.Map;

public class MapDto {

  Map<Integer, Integer> productInfo;

  public MapDto() {
  }

  public Map<Integer, Integer> getProductInfo() {
    return productInfo;
  }

  public void setProductInfo(Map<Integer, Integer> productInfo) {
    this.productInfo = productInfo;
  }
}
