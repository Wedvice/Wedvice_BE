package com.wedvice.couple.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class PartnerNotFoundException extends CustomException {
    public PartnerNotFoundException() {
        super("상대방 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
    }
}