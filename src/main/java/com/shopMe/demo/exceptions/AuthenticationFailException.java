package com.shopMe.demo.exceptions;

public class AuthenticationFailException extends Exception {
    public AuthenticationFailException(String msg) {
        super(msg);
    }
}
