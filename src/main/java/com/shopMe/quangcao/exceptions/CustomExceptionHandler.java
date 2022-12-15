package com.shopMe.quangcao.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
       Map<String, Object> responseBody = new LinkedHashMap<>();

       responseBody.put("timestamp", new Date());
       responseBody.put("status",status.value());

       List<FieldError> fieldErrors= ex.getBindingResult().getFieldErrors();

       List<String> listError = new ArrayList<>();
       for (FieldError fieldError : fieldErrors){
           String errorMessage = fieldError.getDefaultMessage();
           listError.add(errorMessage);
       }

       responseBody.put("error",listError);

       return new ResponseEntity<>(responseBody,headers,status);
    }
}
