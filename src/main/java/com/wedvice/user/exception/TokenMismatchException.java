package com.wedvice.user.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TokenMismatchException extends CustomException {
    public TokenMismatchException() {
        super("리프래쉬 토큰이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
    }
}