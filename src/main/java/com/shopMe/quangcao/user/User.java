package com.shopMe.quangcao.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopMe.quangcao.order.Order;
import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.common.Constants;
import com.shopMe.quangcao.role.Role;
import com.shopMe.quangcao.user.userDTO.UpdateUserDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "first_name", nullable = false)
  @NotBlank(message = "First name cannot be null")
  @Length(min = 3, max = 255, message = "First name must have 3-255 characters")
  private String firstName;

  @Column(name = "last_name", nullable = false)
  @NotBlank(message = "Last name cannot be null")
  @Length(min = 3, max = 255, message = "Last name must have 3-255 characters")
  private String lastName;

  @Column(name = "email", unique = true)
  @Email(message = "Please provide a valid email address")
  private String email;

  @Column(name = "phoneNumber", length = 20, unique = true)
  private String phoneNumber;

  @Column(name = "avatar")
  private String avatar;

  @JsonIgnore
  @Column(name = "password", length = 25, nullable = false)
  @NotBlank(message = "Mật khẩu không được để trống")
  @Length(min = 8,message = "Mật khẩu phải có ít nhất 8 ký tự")
  @Length(max = 255,message = "Mật khẩu không được quá 255 ký tự")
  private String password;

  @Column(name = "created_date")
  private Date CreatedDate;
  @Column(name = "enabled")
  private Boolean enabled;

  @Column(name = "email_verified")
  private Boolean emailVerified;

  @Column(name = "email_verify_code")
  private String emailVerifyCode;
  @ManyToMany
  @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "users_id"), inverseJoinColumns = @JoinColumn(name = "roles_id"))
  private Set<Role> roles = new HashSet<>();


  @ManyToMany
  private Set<Product> wishlist = new HashSet<>();


  public User() {
  }

  public User(Integer id) {
    this.id = id;
  }

  @JsonIgnore
  @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Order> orders;


  public User(String firstName, String lastName, String email, String phoneNumber,
      String password) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.password = password;
  }

  public User(String firstName, String lastName, String email, String phoneNumber,
      String avatar, String password, Date createdDate, Boolean enabled
  ) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.avatar = avatar;
    this.password = password;
    CreatedDate = createdDate;
    this.enabled = enabled;

  }

  public void Update(UpdateUserDto dto) {
    this.firstName = dto.getFirstName();
    this.lastName = dto.getLastName();
    this.email = dto.getEmail();
  }

  public Set<Product> getWishlist() {
    return wishlist;
  }

  public void setWishlist(Set<Product> wishlist) {
    this.wishlist = wishlist;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
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

  public void addProduct(Product product) {
    this.wishlist.add(product);
  }

  public void removeProduct(Product product) {
    this.wishlist.remove(product);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    for (Role role : roles) {
      authorities.add(new SimpleGrantedAuthority(role.getName()));
    }
    return authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.phoneNumber;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  public void addRole(Role role) {
    this.roles.add(role);
  }

  public void removeRole(Role role) {
    this.roles.remove(role);
  }

  public Date getCreatedDate() {
    return CreatedDate;
  }

  public void setCreatedDate(Date createdDate) {
    CreatedDate = createdDate;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }


  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmailVerifyCode() {
    return emailVerifyCode;
  }

  public void setEmailVerifyCode(String emailVerifyCode) {
    this.emailVerifyCode = emailVerifyCode;
  }

  public Boolean getEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(Boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  @Transient
  public String getPhotosImagePath() {
    if (id == null || avatar == null) {
      return "http://localhost:8082/user-images/default-user.png";
    }
    return "http://localhost:8082/user-images/" + this.id + "/" + this.avatar;
  }

  @Override
  public String toString() {
    return '{' +
        "\"id\":" + id +
        ", \"firstName\":\"" + firstName + '\"' +
        ", \"lastName\":\"" + lastName + '\"' +
        ", \"email\":\"" + email + '\"' +
        ", \"phoneNumber\":\"" + phoneNumber + '\"' +
        '}';
  }


}