package com.shopMe.demo.Product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopMe.demo.Address.Address;
import com.shopMe.demo.Address.AddressPoint.AddressPoint;
import com.shopMe.demo.Category.Category;
import com.shopMe.demo.OrderDetail.OrderDetail;
import com.shopMe.demo.Product.dto.AddProductDto;
import com.shopMe.demo.Product.dto.UpdateProductDto;
import com.shopMe.demo.common.Constants;
import com.shopMe.demo.user.User;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "product")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  private String description;

  private float price;

  @Transient
  private ProductStatus status;

  private String image;

  @Column(name = "lat", nullable = false)
  private Double lat;

  @Column(name = "lng", nullable = false)
  private Double lng;

  @Column(name = "number", nullable = false)
  private Double number;
  @ManyToOne
  @JoinColumn(name = "address_id")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Address address;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;


  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<OrderDetail> orderDetails;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Transient
  private Date expiredDate;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Transient
  private Date startDate;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Transient
  private Integer month;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Transient
  private String user;

  @Transient
  private boolean isInCart;

  @Transient
  private boolean isInWishList;

  @Transient
  private Set<AddressPoint> points;


  @JsonIgnore
  @ManyToMany(mappedBy = "wishlist")
  Set<User> usersWL;

  public Date getExpiredDate() {

    if (showOrderDetail() != null) {
      return showOrderDetail().getExpiredDate();
    } else {
      return null;
    }
  }

  public Set<AddressPoint> getPoints() {
    if (address != null) {
    Set<AddressPoint> list = address.getAddressPoints().stream().sorted(Comparator.comparingDouble(AddressPoint::getNumber)).collect(
        Collectors.toCollection(LinkedHashSet::new));
    int min =(int) (number -0.5);
    int max =(int) (number +0.5);
      list = list.stream().filter(point -> point.getNumber() == min || point.getNumber() == max)
          .collect(
              Collectors.toSet());
      return list;
    } else {
      return null;
    }


  }

  public Product() {
  }

  public Product(AddProductDto add) {
    this.name = add.getName();
    this.description = add.getDescription();
    this.price = add.getPrice();
    this.lat = add.getLat();
    this.lng = add.getLng();
  }

  public Product copyUpdate(UpdateProductDto update) {
    this.id = update.getId();
    this.name = update.getName();
    this.description = update.getDescription();
    this.price = update.getPrice();
    this.status = update.getStatus();
    this.lat = update.getLat();
    this.lng = update.getLng();
    return this;
  }


  public Double getNumber() {
    return number;
  }

  public void setNumber(Double number) {
    this.number = number;
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

  public Product(Integer id) {
    this.id = id;
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

  public Set<User> getUsersWL() {
    return usersWL;
  }

  public void setUsersWL(Set<User> usersWL) {
    this.usersWL = usersWL;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public float getPrice() {
    return price;

  }

  public Double getLat() {
    return lat;
  }

  public void setLat(Double lat) {
    this.lat = lat;
  }

  public Double getLng() {
    return lng;
  }

  public void setLng(Double lng) {
    this.lng = lng;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public ProductStatus getStatus() {
    if (showOrderDetail() == null) {
      return status = ProductStatus.AVAILABLE;
    } else {
      return status = ProductStatus.HIRING;
    }

  }

  public Date getStartDate() {
    if (showOrderDetail() == null) {
      return null;
    } else {
      return showOrderDetail2().getStartDate();
    }
  }

  public Integer getMonth() {
    return orderDetails.stream()
        .filter(Objects::nonNull)
        .filter(orderDetail -> orderDetail.getExpiredDate() != null)
        .filter(orderDetail -> orderDetail.getExpiredDate().after(new Date()))
        .map(OrderDetail::getMonth)
        .reduce(0, Integer::sum);
  }

  public String getUser() {
    if (showOrderDetail() == null) {
      return null;
    } else {
      return showOrderDetail().getOrders().getUser().toString();
    }
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }


  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }


  public List<OrderDetail> getOrderDetails() {
    return orderDetails;
  }

  public void setOrderDetails(List<OrderDetail> orderDetails) {
    this.orderDetails = orderDetails;
  }

  @Override
  public String toString() {
    return '{' +
        "\"id\":" + id +
        ", \"name\":\"" + name + '\"' +
        ", \"description\":\"" + description + '\"' +
        ", \"price\":" + price +
        ", \"status\":\"" + status + '\"' +
        ", \"street\":\"" + address.getStreet() + '\"' +
        '}';

  }


  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  @Transient
  public String getPhotosImagePath() {
    if (id == null || image == null) {
      return Constants.S3_BASE_URI + "/default-images/default-user.png";
    }
    return Constants.S3_BASE_URI + "/product-images/" + this.id + "/" + this.image;
  }


  private OrderDetail showOrderDetail() {
    return orderDetails.stream()
        .filter(Objects::nonNull)
        .filter(orderDetail -> orderDetail.getExpiredDate() != null)
        .filter(orderDetail -> orderDetail.getExpiredDate().after(new Date()))
        .max(Comparator.comparing(OrderDetail::getExpiredDate)).orElse(null);
  }

  private OrderDetail showOrderDetail2() {
    return orderDetails.stream()
        .filter(Objects::nonNull)
        .filter(orderDetail -> orderDetail.getExpiredDate() != null)
        .filter(orderDetail -> orderDetail.getExpiredDate().after(new Date()))
        .min(Comparator.comparing(OrderDetail::getExpiredDate)).orElse(null);
  }


}
