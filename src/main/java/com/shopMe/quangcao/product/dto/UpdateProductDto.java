package com.shopMe.quangcao.product.dto;

import com.shopMe.quangcao.product.ProductStatus;

public class UpdateProductDto {

  private Integer id;

  private String name;

  private String description;

  private float price;

  private Integer addressId;

  private Integer categoryId;

  private ProductStatus status;

  private Double lat;

  private Double lng;

  private Double num1;

  private Double num2;

  public Double getLat() {
    return lat;
  }

  public void setLat(Double lat) {
    this.lat = lat;
  }

  public Double getLng() {
    return lng;
  }

  public void setLng(Double lng) {
    this.lng = lng;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }


  public Integer getAddressId() {
    return addressId;
  }

  public void setAddressId(Integer addressId) {
    this.addressId = addressId;
  }

  public ProductStatus getStatus() {
    return status;
  }

  public void setStatus(ProductStatus status) {
    this.status = status;
  }

  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  public Double getNum1() {
    return num1;
  }

  public void setNum1(Double num1) {
    this.num1 = num1;
  }

  public Double getNum2() {
    return num2;
  }

  public void setNum2(Double num2) {
    this.num2 = num2;
  }
}

