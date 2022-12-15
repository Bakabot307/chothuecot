package com.shopMe.quangcao.order.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class Reorder {
  
  Map<Integer, Integer> productInfo = new LinkedHashMap<>();


  public Map<Integer, Integer> getProductInfo() {
    return productInfo;
  }

  public void setProductInfo(Map<Integer, Integer> productInfo) {
    this.productInfo = productInfo;
  }


}
