package com.shopMe.quangcao.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

  @ExceptionHandler(value = ProductNotExistException.class)
  public final ResponseEntity<String> handleUpdateFailException(
      ProductNotExistException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = NullPointerException.class)
  public final ResponseEntity<String> HandleNullPointerException(
      NullPointerException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = OrderCantExtendException.class)
  public final ResponseEntity<String> HandleOrderCantExtendException(
      OrderCantExtendException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = IllegalStateException.class)
  public final ResponseEntity<String> IllegalStateException(
      IllegalStateException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }


}
