package com.shopMe.demo.Overview;

import com.shopMe.demo.Product.Product;
import java.util.Map;

public class OverviewProductDto {

  Map<Product, Long> map;
  int totalProduct;
  int maxPage;
  int currentPage;

  public OverviewProductDto() {
  }

  public OverviewProductDto(Map<Product, Long> map, int totalProduct, int maxPage,
      int currentPage) {
    this.map = map;
    this.totalProduct = totalProduct;
    this.maxPage = maxPage;
    this.currentPage = currentPage;
  }


  public Map<Product, Long> getMap() {
    return map;
  }

  public void setMap(Map<Product, Long> map) {
    this.map = map;
  }

  public int getTotalProduct() {
    return totalProduct;
  }

  public void setTotalProduct(int totalProduct) {
    this.totalProduct = totalProduct;
  }

  public int getMaxPage() {
    return maxPage;
  }

  public void setMaxPage(int maxPage) {
    this.maxPage = maxPage;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }
}
