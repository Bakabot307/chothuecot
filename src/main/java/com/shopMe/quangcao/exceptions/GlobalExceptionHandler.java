package com.shopMe.quangcao.exceptions;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public ErrorInfo AccessDeniedException(HttpServletRequest request, HttpServletResponse response,
      Exception exception) {

    return new ErrorInfo(request, exception);
  }

  @ExceptionHandler(AuthenticationFailException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorInfo AuthenticationFailException(HttpServletRequest request,
      HttpServletResponse response,
      Exception exception) {

    return new ErrorInfo(request, exception);
  }


  @ExceptionHandler(Exception500.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorInfo ServerError(HttpServletRequest request, HttpServletResponse response,
      Exception exception) {
    return
        new ErrorInfo(new Date(), request.getRequestURL().toString(),
            "Lỗi hệ thống, vui lòng thử lại sau."
        );
  }

  @ExceptionHandler(value = WebImageException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorInfo HandleWebImageException(
      HttpServletRequest request, HttpServletResponse response,
      Exception exception) {
    return new ErrorInfo(request, exception);
  }

  @ExceptionHandler(value = CustomException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo CustomException(
      HttpServletRequest request, HttpServletResponse response,
      Exception exception) {
    return new ErrorInfo(request, exception);
  }

  @ExceptionHandler(value = ProductExistedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo ProductNotExistException(
      HttpServletRequest request, HttpServletResponse response,
      Exception exception) {
    return new ErrorInfo(request, exception);
  }
}
