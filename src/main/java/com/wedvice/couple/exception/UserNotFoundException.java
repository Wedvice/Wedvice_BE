package com.wedvice.couple.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super("존재하지 않는 유저와의 매칭입니다.", HttpStatus.NOT_FOUND);
    }
}
