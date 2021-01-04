package com.luisfn.backendjava.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class AppExceptionHandler {
    //Para controlar una excepcion en particular
    @ExceptionHandler(value = EmailExistsException.class)
    public ResponseEntity<Object> handleEmailExistsException(EmailExistsException ex, WebRequest webRequest) {

        ErrorMessage em = new ErrorMessage(new Date(), ex.getMessage());
        return new ResponseEntity<>(em, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Para controlar el resto de excepciones
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(Exception ex, WebRequest webRequest) {

        ErrorMessage em = new ErrorMessage(new Date(), ex.getMessage());
        return new ResponseEntity<>(em, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
