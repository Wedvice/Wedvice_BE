package com.wedvice.controller.testuser;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TestUserNotMatchedCreatedUserException extends CustomException {

    public TestUserNotMatchedCreatedUserException() {
        super("테스트를 생성한 유저와 다릅니다.", HttpStatus.BAD_REQUEST);
    }
}
