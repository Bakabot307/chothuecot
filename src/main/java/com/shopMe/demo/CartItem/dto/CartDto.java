package com.shopMe.demo.CartItem.dto;

import com.shopMe.demo.Address.AddressPoint.AddressPoint;
import com.shopMe.demo.CartItem.CartItem;
import java.util.List;
import java.util.Map;

public class CartDto {

  Map<AddressPoint, AddressPoint> addressPointMap;
  private List<CartItem> cartItems;

  public CartDto() {
  }

  public Map<AddressPoint, AddressPoint> getAddressPointMap() {
    return addressPointMap;
  }

  public void setAddressPointMap(
      Map<AddressPoint, AddressPoint> addressPointMap) {
    this.addressPointMap = addressPointMap;
  }

  public List<CartItem> getCartItems() {
    return cartItems;
  }

  public void setCartItems(List<CartItem> cartItems) {
    this.cartItems = cartItems;
  }
}
