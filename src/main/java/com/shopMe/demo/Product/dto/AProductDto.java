package com.shopMe.demo.Product.dto;

import com.shopMe.demo.Address.Address;
import com.shopMe.demo.Category.Category;
import com.shopMe.demo.Product.Product;
import com.shopMe.demo.Product.ProductStatus;

public class AProductDto {

  private Integer id;

  private String name;

  private String description;

  private float price;


  private ProductStatus status;

  private Address address;

  private Category category;

  private String image;

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


  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  @Override
  public String toString() {
    return "AProductDto{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", price=" + price +
        ", status=" + status +
        ", address=" + address +
        '}';
  }
}
