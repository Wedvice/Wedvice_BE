package com.wedvice.couple.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class PartnerMustEnterMatchCode extends CustomException {
    public PartnerMustEnterMatchCode() {
        super("매치 코드가 입력되지 않았습니다.", HttpStatus.BAD_REQUEST);
    }
}
