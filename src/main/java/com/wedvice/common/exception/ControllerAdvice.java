package com.wedvice.common.exception;

import com.wedvice.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handle(CustomException e) {
        return ResponseEntity.status(e.httpStatus)
                .body(ApiResponse.error(e.httpStatus.value(), e.getMessage()));
    }
}
