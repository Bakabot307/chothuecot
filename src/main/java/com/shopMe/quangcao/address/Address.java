package com.shopMe.quangcao.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopMe.quangcao.address.AddressPoint.AddressPoint;
import com.shopMe.quangcao.address.dto.AddAddressDto;
import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.common.Constants;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "address")
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Tên đường không được để trống")
  private String street;
  @NotBlank(message = "Tên thành phố không được để trống")
  private String city;
  @NotBlank(message = "Thông tin chi tiết không được để trống")
  private String description;

  private String image;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Product> products;

  @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<AddressPoint> addressPoints;

  public Address() {
  }

  public Address(AddAddressDto a) {
    this.street = a.getStreet();
    this.city = a.getCity();
    this.description = a.getDescription();
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

  public String getFullAddress() {
    return street + " " + city;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStreetOrCity(String test) {
    if (Objects.equals(test, "street")) {
      return getStreet();
    }
    return getCity();
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public Set<Product> getProducts() {
    return products;
  }

  public void setProducts(Set<Product> products) {
    this.products = products;
  }

  @Transient
  public String getPhotosImagePath() {
    if (id == null || image == null) {
      return "http://localhost:8082/address-images/default-address.png";
    }
    return "http://localhost:8082/address-images/" + this.id + "/" + this.image;
  }

  public Set<AddressPoint> getAddressPoints() {
    return addressPoints;
  }

  public void setAddressPoints(Set<AddressPoint> addressPoints) {
    this.addressPoints = addressPoints;
  }

  @Override
  public String toString() {
    return '{' +
        "\"id\":" + id +
        ", \"street\":\"" + street + '\"' +
        ", \"city\":\"" + city + '\"' +
        '}';
  }
}
