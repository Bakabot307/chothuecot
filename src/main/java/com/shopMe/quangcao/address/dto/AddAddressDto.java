package com.shopMe.quangcao.address.dto;

import javax.validation.constraints.NotBlank;

public class AddAddressDto {

  @NotBlank(message = "Tên đường không được để trống")
  private String street;
  @NotBlank(message = "Tên thành phố không được để trống")
  private String city;
  @NotBlank(message = "Thông tin chi tiết không được để trống")
  private String description;


  public AddAddressDto() {
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


}
