package com.shopMe.quangcao.wishList;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
class WishListKey implements Serializable {

  @Column(name = "product_id")
  Integer productId;

  @Column(name = "users_id")
  Integer userId;

  public WishListKey() {
  }

  public Integer getProductId() {
    return productId;
  }

  public void setProductId(Integer productId) {
    this.productId = productId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    WishListKey that = (WishListKey) o;

    if (getProductId() != null ? !getProductId().equals(that.getProductId())
        : that.getProductId() != null) {
      return false;
    }
    return getUserId() != null ? getUserId().equals(that.getUserId()) : that.getUserId() == null;
  }

  @Override
  public int hashCode() {
    int result = getProductId() != null ? getProductId().hashCode() : 0;
    result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
    return result;
  }
}