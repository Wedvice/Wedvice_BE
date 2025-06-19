package com.wedvice.user.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TokenInvalidException extends CustomException {
    public TokenInvalidException() {
        super("리프래쉬 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
    }
}
