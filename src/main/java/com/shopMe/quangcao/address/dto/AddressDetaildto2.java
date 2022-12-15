package com.shopMe.quangcao.address.dto;

import com.shopMe.quangcao.address.Address;
import com.shopMe.quangcao.category.Category;
import com.shopMe.quangcao.product.Product;
import java.util.List;
import java.util.Map;

public class AddressDetaildto2 {

  private Map<Category, List<Product>> products;
  private Address address;


  public Map<Category, List<Product>> getProducts() {
    return products;
  }

  public void setProducts(
      Map<Category, List<Product>> products) {
    this.products = products;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }
  

  public AddressDetaildto2() {
  }
}
