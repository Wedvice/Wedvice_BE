package com.wedvice.couple.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AlreadyMatchedException extends CustomException {
    public AlreadyMatchedException() {
        super("이미 닉네임 설정 및 신랑 신부 역할이 입력되었습니다.", HttpStatus.CONFLICT);
    }
}
