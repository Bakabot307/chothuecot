package com.shopMe.quangcao.webImage;

import com.shopMe.quangcao.common.Constants;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "web_image")
public class WebImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Integer id;

  private String image;
  @NotBlank(message = "Category không được để trống")
  private String category;

  @Column(name = "active")
  private boolean active = true;

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
      return "http://localhost:8082//web-images/default-web-image.png";
    }
    return "http://localhost:8082/web-images/" + this.id + "/" + this.image;
  }
}
