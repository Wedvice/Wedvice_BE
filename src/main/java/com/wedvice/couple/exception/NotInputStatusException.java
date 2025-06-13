package com.wedvice.couple.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NotInputStatusException extends CustomException {
    private static final String s = "아직 필수 입력 정보가 남았습니다.";

    public NotInputStatusException() {
        super(s, HttpStatus.BAD_REQUEST);
    }
}
