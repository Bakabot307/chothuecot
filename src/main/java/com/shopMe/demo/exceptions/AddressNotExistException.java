package com.shopMe.demo.exceptions;

public class AddressNotExistException extends Exception{
    public AddressNotExistException(String message) {
        super(message);
    }
}
