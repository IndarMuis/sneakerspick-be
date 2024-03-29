package com.sneakerspick.controller;

import com.sneakerspick.dto.response.WebResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<?>> constrainViolationException(ConstraintViolationException exc) {
        WebResponse<?> response = WebResponse.builder()
                .message("error")
                .code(HttpStatus.BAD_REQUEST.value())
                .errors(exc.getMessage()).build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<?>> responseStatusException(ResponseStatusException exc) {
        WebResponse<?> response = WebResponse.builder()
                .message(exc.getMessage())
                .code(exc.getStatusCode().value())
                .errors(exc.getReason()).build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<?>> errorException(Exception exc) {
        WebResponse<?> response = WebResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errors(exc.getMessage()).build();
        return ResponseEntity.internalServerError().body(response);
    }



}
