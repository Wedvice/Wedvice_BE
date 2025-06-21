package com.wedvice.couple.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class SameRoleException extends CustomException {
    private static final String s = "같은 성별일 수 없습니다.";

    public SameRoleException() {
        super(s, HttpStatus.BAD_REQUEST);
    }
}
