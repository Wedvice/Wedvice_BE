package com.wedvice.subtask.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NotExistRoleException extends CustomException {
    private static final String message = "존재하지 않는 역할입니다.";

    public NotExistRoleException() {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
