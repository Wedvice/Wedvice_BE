package com.wedvice.user.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TokenNotFoundException extends CustomException {
    private static final String message = "토큰이 존재하지 않습니다.";

    public TokenNotFoundException() {
        super(message, HttpStatus.NOT_FOUND);
    }
}
