package com.example.tradedemo.common.exception;

import com.example.tradedemo.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse> handleServiceException(ServiceException e) {

        HttpStatus httpStatus = e.getErrorEnum().getHttpStatus();
        String error = e.getMessage();

        return ResponseEntity.status(httpStatus).body(ApiResponse.fail(String.valueOf(httpStatus.value()), error));
    }
}
