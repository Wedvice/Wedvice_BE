package com.wedvice.common.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class CustomException extends RuntimeException {

    public HttpStatus httpStatus;

    public CustomException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Map<String, Object> toExampleJson() {
        log.info("[CustomException] 에러발생 : {}", this.getClass());
        return Map.of(
            "code", httpStatus.value(),
            "message", getMessage(),
            "data", new HashMap<>(
            ));
    }
}
