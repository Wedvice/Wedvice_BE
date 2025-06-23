package com.wedvice.user.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnknownTokenException extends CustomException {
    public UnknownTokenException() {
        super("알 수 없는 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}