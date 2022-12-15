package com.shopMe.quangcao.address.AddressPoint.dto;

import javax.persistence.Column;

public class AddressPointDto {

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "lat", nullable = false)
  private Double lat;

  @Column(name = "lng", nullable = false)
  private Double lng;


  @Column(name = "address_id", nullable = false)
  private Integer addressId;

  public AddressPointDto() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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


  public Integer getAddressId() {
    return addressId;
  }

  public void setAddressId(Integer addressId) {
    this.addressId = addressId;
  }
}
