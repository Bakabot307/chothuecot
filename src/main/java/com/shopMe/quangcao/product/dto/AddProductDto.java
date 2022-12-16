package com.shopMe.quangcao.product.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AddProductDto {

  @NotBlank(message = "Tên sản phẩm không được để trống")
  private String name;

  @NotBlank(message = "Mô tả sản phẩm không được để trống")
  private String description;

  @NotNull(message = "Giá sản phẩm không được để trống")
  @Column(name = "price")
  private float price;

  private Integer addressId;

  private Integer categoryId;

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


  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }


}
