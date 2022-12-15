package com.shopMe.quangcao.orderDetail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopMe.quangcao.order.Order;
import com.shopMe.quangcao.product.Product;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "order_detail")
public class OrderDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "product_id")
  private Product product;

  private Integer month;
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "orders_id")
  private Order orders;
  private Date startDate;

  private Date expiredDate;

  public OrderDetail() {
  }

  public OrderDetail(OrderDetail orderDetail) {
    this.product = orderDetail.getProduct();
    this.month = orderDetail.getMonth();
    this.startDate = orderDetail.getStartDate();
    this.expiredDate = orderDetail.getExpiredDate();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Order getOrders() {
    return orders;
  }

  public void setOrders(Order orders) {
    this.orders = orders;
  }


  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Integer getMonth() {
    return month;
  }

  public void setMonth(Integer month) {
    this.month = month;
  }
  

  public Date getExpiredDate() {
    return expiredDate;
  }

  public void setExpiredDate(Date expiredDate) {
    this.expiredDate = expiredDate;
  }

  @Override
  public String toString() {
    return "OrderDetail{" +
        "id=" + id +
        ", product=" + product +
        ", day=" + month +
        ", orders=" + orders +
        ", startDate=" + startDate +
        '}';
  }
}
