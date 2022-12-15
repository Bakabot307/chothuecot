package com.shopMe.quangcao.user.userDTO;


import com.shopMe.quangcao.user.User;
import com.shopMe.quangcao.role.Role;

import java.util.Date;
import java.util.Set;

public class UserProfileResponse {
    private Integer id;


    private String firstName;


    private String lastName;


    private String email;


    private String phoneNumber;


    private String avatar;


    private Date createdDate;

    private Boolean enabled;


    private Boolean phoneEnabled;


    private Boolean emailVerified;


   private String[] roles;


    public UserProfileResponse() {
    }


    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.avatar = user.getPhotosImagePath();
        this.createdDate = user.getCreatedDate();
        this.enabled = user.getEnabled();
        this.roles = replaceRole(user.getRoles());
    }
public String[] replaceRole(Set<Role> role){
        String roleString = role.toString();
        roleString = roleString.replace("[", "").replace("]", "");
        String[] roleNames = roleString.split(",");
        return roleNames;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getPhoneEnabled() {
        return phoneEnabled;
    }

    public void setPhoneEnabled(Boolean phoneEnabled) {
        this.phoneEnabled = phoneEnabled;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }


    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }
}
