package com.wedvice.couple.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class MatchCodeExpiredException extends CustomException {
    public MatchCodeExpiredException(String matchCode) {
        super("만료되었거나 존재하지 않는 매치 코드입니다. [%s]".formatted(matchCode), HttpStatus.NOT_FOUND);
    }
}