package com.shopMe.quangcao.wishList;

import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.user.User;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public
class WishList {

  @EmbeddedId
  WishListKey id;

  @ManyToOne
  @MapsId("productId")
  @JoinColumn(name = "product_id")
  Product product;

  @ManyToOne
  @MapsId("userId")
  @JoinColumn(name = "users_id")
  User user;

  public WishList() {
  }

  public WishListKey getId() {
    return id;
  }

  public void setId(WishListKey id) {
    this.id = id;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "WishList{" +
        "id=" + id +
        ", product=" + product +
        ", user=" + user +
        '}';
  }
}
