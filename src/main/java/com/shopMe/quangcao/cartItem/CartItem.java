package com.shopMe.quangcao.cartItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.user.User;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "cart_item")
public class CartItem {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;
  
  private Integer month;

  @Transient
  public float getSubtotal() {
    return product.getPrice() * month;
  }

  public CartItem() {
  }

  public Integer getId() {

    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Integer getMonth() {
    return month;
  }

  public void setMonth(Integer month) {
    this.month = month;
  }

  @Transient
  public int hiringProductNumber;


}
