package com.shopMe.quangcao.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.shopMe.quangcao.orderDetail.OrderDetail;
import com.shopMe.quangcao.user.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;


  private Date orderTime;


  private Date confirmedTime;

  private Integer quantity;
  private float total;


  private String orderCode;


  @JsonIgnore
  @ManyToOne()
  @JoinColumn(name = "users_id")
  private User user;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private Set<OrderDetail> orderDetail = new HashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @OrderBy("updatedTime ASC")
  private List<OrderTrack> orderTracks = new ArrayList<>();

  private Date cancelTime;

  public Order() {
  }


  public Order(Order order) {
    this.quantity = order.getQuantity();
    this.total = order.getTotal();
    this.user = order.getUser();
    this.orderDetail = order.getOrderDetail();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public float getTotal() {
    return total;
  }

  public void setTotal(float total) {
    this.total = total;
  }


  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }


  public Set<OrderDetail> getOrderDetail() {
    return orderDetail;
  }

  public void setOrderDetail(Set<OrderDetail> orderDetail) {
    this.orderDetail = orderDetail;
  }


  public Date getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(Date orderTime) {
    this.orderTime = orderTime;
  }

  public Date getConfirmedTime() {
    return confirmedTime;
  }

  public void setConfirmedTime(Date confirmedTime) {
    this.confirmedTime = confirmedTime;
  }

  public String getOrderCode() {
    return orderCode;
  }

  public void setOrderCode(String orderCode) {
    this.orderCode = orderCode;
  }

  public List<OrderTrack> getOrderTracks() {
    return orderTracks;
  }

  public void setOrderTracks(List<OrderTrack> orderTracks) {
    this.orderTracks = orderTracks;
  }


  public Date getCancelTime() {
    return cancelTime;
  }

  public void setCancelTime(Date cancelTime) {
    this.cancelTime = cancelTime;
  }

  @Transient
  private boolean hasChildren;

  @Transient
  private boolean hasParent;

  @Transient
  private Integer parentId;

  @Transient
  private boolean extendable;

  @Transient
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @Transient
  @Enumerated(EnumType.STRING)
  private OrderStatus orderType;

  @Transient
  private final ArrayList<OrderStatus> trackerStatus = new ArrayList<>();

  @Transient
  private boolean isUserConfirmed;


  public ArrayList<OrderStatus> getTracker() {
    return showOrderTrackerStatus();
  }


  private ArrayList<OrderStatus> showOrderTrackerStatus() {
    for (OrderTrack orderTrack : orderTracks.stream()
        .sorted(Comparator.comparing(OrderTrack::getId)).toList()) {
      if (trackerStatus.contains(orderTrack.getStatus())) {
        continue;
      }
      trackerStatus.add(orderTrack.getStatus());
    }
    return trackerStatus;
  }

  private OrderStatus showCurrentOrderStatus() {
    if (showOrderTrackerStatus().stream().reduce((first, second) -> second).get()
        == OrderStatus.CANCELLED) {
      return OrderStatus.CANCELLED;
    }
    if (orderDetail.stream()
        .filter(orderDetail -> orderDetail.getExpiredDate() != null)
        .noneMatch(orderDetail -> orderDetail.getExpiredDate().after(new Date()))) {
      return OrderStatus.DONE;
    } else {
      return showOrderTrackerStatus().stream().reduce((first, second) -> second).orElse(null);
    }
  }

  private OrderStatus showFirstOrderStatus() {
    return showOrderTrackerStatus().stream().reduce((first, second) -> first).orElse(null);
  }


  public OrderStatus getStatus() {
    return showCurrentOrderStatus();
  }

  public OrderStatus getOrderType() {
    return showFirstOrderStatus();
  }

  public boolean isUserConfirmed() {
    for (OrderStatus status : showOrderTrackerStatus()) {
      if (status != OrderStatus.USER_CONFIRMED) {
        return false;
      }
    }
    return true;
  }
}
