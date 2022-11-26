package com.shopMe.demo.Address.dto;

import com.shopMe.demo.Address.Address;
import com.shopMe.demo.Product.Product;
import java.util.List;

public class AddressDetaildto {

  private List<Product> product;

  private Address address;

  private boolean isInCart;

  private boolean isInWishList;


  public List<Product> getProduct() {
    return product;
  }

  public void setProduct(List<Product> product) {
    this.product = product;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public boolean isInCart() {
    return isInCart;
  }

  public void setInCart(boolean inCart) {
    isInCart = inCart;
  }

  public boolean isInWishList() {
    return isInWishList;
  }

  public void setInWishList(boolean inWishList) {
    isInWishList = inWishList;
  }

  public AddressDetaildto() {
  }
}
