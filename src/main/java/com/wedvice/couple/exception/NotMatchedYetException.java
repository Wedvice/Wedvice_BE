package com.wedvice.couple.exception;


import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NotMatchedYetException extends CustomException {
    public NotMatchedYetException() {
        super("커플 매칭이 되지 않았습니다.", HttpStatus.BAD_REQUEST);
    }
}