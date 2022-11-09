//package com.shopMe.demo.model;
//
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.shopMe.demo.user.User;
//
//import javax.persistence.*;
//import java.util.Date;
//
//@Entity
//public class Request {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Integer id;
//
//    private String status;
//
//    private double sta;
//
//    private double money;
//
//    private Date createdDate;
//
//    private Date checkedDate;
//
//    private String message;
//
//    private String type;
//
//    @JsonIgnore
//    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
//    @JoinColumn(nullable = false, name = "user_id")
//    private User user;
//
//    @JsonIgnore
//    @OneToOne(targetEntity = Wallet.class, fetch = FetchType.EAGER)
//    @JoinColumn(nullable = false, name = "wallet_id")
//    private Wallet wallet;
//
//
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public Request() {
//    }
//
//    public Request(String status, double sta, double money, User user, Wallet wallet) {
//        this.status = status;
//        this.sta = sta;
//        this.money = money;
//        this.createdDate = new Date();
//        this.user = user;
//        this.wallet = wallet;
//    }
//
//    public double getSta() {
//        return sta;
//    }
//
//    public void setSta(double sta) {
//        this.sta = sta;
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
//    public Wallet getWallet() {
//        return wallet;
//    }
//
//    public void setWallet(Wallet wallet) {
//        this.wallet = wallet;
//    }
//
//
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public double getMoney() {
//        return money;
//    }
//
//    public void setMoney(double money) {
//        this.money = money;
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
//    public Date getCheckedDate() {
//        return checkedDate;
//    }
//
//    public void setCheckedDate(Date checkedDate) {
//        this.checkedDate = checkedDate;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//}
