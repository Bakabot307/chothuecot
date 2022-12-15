package com.shopMe.quangcao.product.dto;

import com.shopMe.quangcao.address.Address;
import com.shopMe.quangcao.category.Category;
import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.product.ProductStatus;

public class AProductDto {

  private Integer id;

  private String name;

  private String description;

  private float price;


  private ProductStatus status;

  private Address address;

  private Category category;

  private String image;

  private Double lat;

  private Double lng;

  private Double number;

  public AProductDto() {
  }

  public AProductDto(Product p) {
    this.id = p.getId();
    this.name = p.getName();
    this.description = p.getDescription();
    this.price = p.getPrice();
    this.status = p.getStatus();
    this.address = p.getAddress();
    this.category = p.getCategory();
    this.image = p.getPhotosImagePath();
    this.lat = p.getLat();
    this.lng = p.getLng();
    this.number = p.getNumber();
  }


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

  public ProductStatus getStatus() {
    return status;
  }

  public void setStatus(ProductStatus status) {
    this.status = status;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Double getNumber() {
    return number;
  }

  public void setNumber(Double number) {
    this.number = number;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }


}
