//package com.shopMe.demo.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.shopMe.demo.user.User;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotNull;
//import java.util.Date;
//
//@Entity
//@Table(name = "order_items")
//public class OrderItem {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @Column(name = "quantity")
//    private @NotNull int quantity;
//
//    @Column(name = "staProfit")
//    private @NotNull double staProfit;
//
//    @Column(name = "created_date")
//    private Date createdDate;
//
//    @Column(name = "claim_date")
//    private Date claimDate;
//
//    private String status;
//
//
//    @ManyToOne
//    @JsonIgnore
//    @JoinColumn(name = "users_id", referencedColumnName = "id")
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name = "product_id", referencedColumnName = "id")
//    private Product product;
//
//    public OrderItem(){}
//
//    public OrderItem(int quantity, double staProfit, Date createdDate, String status, User user, Product product) {
//        this.quantity = quantity;
//        this.staProfit = staProfit;
//        this.createdDate = createdDate;
//        this.status = status;
//        this.user = user;
//        this.product = product;
//    }
//
//    public Product getProduct() {
//        return product;
//    }
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
//
//
//    public int getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }
//
//    public double getPrice() {
//        return staProfit;
//    }
//
//    public void setPrice(double price) {
//        this.staProfit = price;
//    }
//
//    public Date getCreatedDate() {
//        return createdDate;
//    }
//
//    public void setCreatedDate(Date createdDate) {
//        this.createdDate = createdDate;
//    }
//
//    public double getStaProfit() {
//        return staProfit;
//    }
//
//    public void setStaProfit(double staProfit) {
//        this.staProfit = staProfit;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public Date getClaimDate() {
//        return claimDate;
//    }
//
//    public void setClaimDate(Date claimDate) {
//        this.claimDate = claimDate;
//    }
//}
//
