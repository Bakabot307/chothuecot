package com.shopMe.demo.user.userDTO;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class SignupDto {
    @Column(name = "first_name",nullable = false,length = 255)
    @NotBlank(message = "First name cannot be null")
    @Length(min = 3,max = 255,message = "First name must have 3-255 characters")
    private String firstName;

    @Column(name = "last_name",nullable = false,length = 255)
    @NotBlank(message = "Last name cannot be null")
    @Length(min = 3,max = 255,message = "Last name must have 3-255 characters")
    private String lastName;

    @Column(name = "email",length = 255,unique = true,nullable = false)
    @Email(message = "Please provide a valid email address")
    private String email;

    @Column(name = "password",length = 255, nullable = false)
    @NotBlank(message = "Password cannot be null")
    @Length(min = 8,message = "Password must have at least 8 characters")
    @Length(max = 255,message = "Last name must have below 255 characters")
    private String password;

    @Column(name = "phoneNumber",length = 20,unique = true,nullable = true)
    @NotBlank(message = "Phone number cannot be null")
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
