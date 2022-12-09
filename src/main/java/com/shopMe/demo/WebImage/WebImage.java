package com.shopMe.demo.WebImage;

import com.shopMe.demo.common.Constants;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "web_image")
public class WebImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Integer id;

  @NotNull(message = "Hình ảnh không được để trống")
  private String image;
  @NotNull(message = "Category không được để trống")
  private String category;

  @Column(name = "active")
  private boolean active;

  public WebImage() {
  }

  public WebImage(String category) {
    this.category = category;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }



  @Transient
  public String getPhotosImagePath() {
    if (id == null || image == null) {
      return Constants.S3_BASE_URI + "/default-images/default-user.png";
    }
    return Constants.S3_BASE_URI + "/web-images/" + this.id + "/" + this.image;
  }
}
