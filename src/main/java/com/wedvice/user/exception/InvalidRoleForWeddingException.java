package com.wedvice.user.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidRoleForWeddingException extends CustomException {

    private static final String MESSAGE = "결혼 관련 역할(GROOM, BRIDE, TOGETHER)이 필요합니다.";

    public InvalidRoleForWeddingException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
