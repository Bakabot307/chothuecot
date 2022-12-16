package com.shopMe.quangcao.user.userDTO;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class SignupDto {
    @Column(name = "first_name",nullable = false,length = 255)
    @NotBlank(message = "Tên không được để trống")
    @Length(min = 3,max = 255,message = "Tên phải từ 3 đến 255 ký tự")
    private String firstName;

    @Column(name = "last_name",nullable = false,length = 255)
    @NotBlank(message = "Họ không được để trống")
    @Length(min = 3,max = 255,message = "Họ phải từ 3 đến 255 ký tự")
    private String lastName;

    @Column(name = "email",length = 255,unique = true,nullable = false)
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không thể để trống")
    private String email;

    @Column(name = "password",length = 255, nullable = false)
    @NotBlank(message = "Mật khẩu không được để trống")
    @Length(min = 8,message = "Mật khẩu phải có ít nhất 8 ký tự")
    @Length(max = 255,message = "Mật khẩu không được quá 255 ký tự")
    private String password;

    @Column(name = "phoneNumber",length = 20,unique = true,nullable = true)
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;
    public SignupDto() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
