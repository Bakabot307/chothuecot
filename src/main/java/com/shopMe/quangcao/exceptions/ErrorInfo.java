package com.shopMe.quangcao.exceptions;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;

public class ErrorInfo {

    private final Date timestamp;
    private final String url;
    private final String message;


    public ErrorInfo(String url, String message, Date timestamp) {
        this.url = url;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ErrorInfo(HttpServletRequest request, Exception ex) {
        this.url = request.getRequestURL().toString();
        this.message = ex.getMessage();
        this.timestamp = new Date();
    }
  

    public ErrorInfo(Date timestamp, String url, String message) {
        this.timestamp = timestamp;
        this.url = url;
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
