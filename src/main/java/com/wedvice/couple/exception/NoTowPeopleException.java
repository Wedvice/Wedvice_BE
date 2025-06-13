package com.wedvice.couple.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoTowPeopleException extends CustomException {
    private static final String s = "커플 정보가 비정상적입니다.";

    public NoTowPeopleException() {
        super(s, HttpStatus.BAD_REQUEST);
    }
}
