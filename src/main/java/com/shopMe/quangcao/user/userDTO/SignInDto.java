package com.shopMe.quangcao.user.userDTO;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

public class SignInDto {
    @Column(name = "phoneNumber",length = 20,unique = true,nullable = true)
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;
    @Column(name = "password",length = 255, nullable = false)
    @NotBlank(message = "Mật khẩu không được để trống")
    @Length(min = 8,message = "Mật khẩu phải có ít nhất 8 ký tự")
    @Length(max = 255,message = "Mật khẩu không được quá 255 ký tự")
    private String password;


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
