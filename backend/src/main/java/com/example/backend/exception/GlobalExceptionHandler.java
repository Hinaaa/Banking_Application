package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    //generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessage> handle(Exception e) {
        ExceptionMessage error = new ExceptionMessage(
                "Error: "+e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name()
        );
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handle(IdNotFoundException e) {
        ExceptionMessage error = new ExceptionMessage("Error: "+ e.getMessage(),
                HttpStatus.NOT_FOUND.name());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ExceptionMessage> handle(InvalidCredentialsException e) {
        ExceptionMessage error = new ExceptionMessage(
                "Error " + e.getMessage(),
                HttpStatus.UNAUTHORIZED.name()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
