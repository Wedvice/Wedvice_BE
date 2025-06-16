package com.wedvice.couple.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidUserAccessException extends CustomException {
    public InvalidUserAccessException() {
        super("유효하지 않은 유저의 접근입니다.", HttpStatus.UNAUTHORIZED);
    }
}
