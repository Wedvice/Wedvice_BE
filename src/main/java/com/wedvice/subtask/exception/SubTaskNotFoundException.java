package com.wedvice.subtask.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class SubTaskNotFoundException extends CustomException {

    public SubTaskNotFoundException() {
        super("해당 subtask가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
    }
}
