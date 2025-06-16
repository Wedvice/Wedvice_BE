package com.wedvice.couple.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class SamePersonMatchException extends CustomException {
    public SamePersonMatchException() {
        super("본인과는 커플이 될 수 없습니다.", HttpStatus.BAD_REQUEST);
    }
}
