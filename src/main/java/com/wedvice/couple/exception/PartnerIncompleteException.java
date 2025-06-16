package com.wedvice.couple.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class PartnerIncompleteException extends CustomException {
    public PartnerIncompleteException() {
        super("아직 상대방의 매칭 정보가 완료되지 않았습니다.", HttpStatus.BAD_REQUEST);
    }
}