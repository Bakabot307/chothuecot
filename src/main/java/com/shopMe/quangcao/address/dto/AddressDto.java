package com.shopMe.quangcao.address.dto;

import com.shopMe.quangcao.address.Address;

public class AddressDto {

  private Integer id;

  private String street;

  private String city;

  private String description;

  private float maxPrice;

  private float minPrice;

  private Integer totalProduct;
  private Integer totalProductAvailable;

  private String image;


  public AddressDto(Address a) {
    this.id = a.getId();
    this.street = a.getStreet();
    this.city = a.getCity();
    this.description = a.getDescription();
    this.image = a.getPhotosImagePath();

  }

  public AddressDto() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public float getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(float maxPrice) {
    this.maxPrice = maxPrice;
  }

  public float getMinPrice() {
    return minPrice;
  }

  public void setMinPrice(float minPrice) {
    this.minPrice = minPrice;
  }


  public Integer getTotalProductAvailable() {
    return totalProductAvailable;
  }

  public void setTotalProductAvailable(Integer totalProductAvailable) {
    this.totalProductAvailable = totalProductAvailable;
  }

  public Integer getTotalProduct() {
    return totalProduct;
  }

  public void setTotalProduct(Integer totalProduct) {
    this.totalProduct = totalProduct;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }
}
