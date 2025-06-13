package com.wedvice.security.excption;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class JwtAuthenticationException extends CustomException {
    private static String message = "올바른 토큰이 아닙니다";

    public JwtAuthenticationException() {
        super(message, HttpStatus.BAD_REQUEST);
    }
}