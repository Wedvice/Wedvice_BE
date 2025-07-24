package com.wedvice.common.exception;

import com.wedvice.common.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiErrorResponse", description = "에러 응답 포맷")
public class ApiErrorResponse extends ApiResponse<Void> {

    public ApiErrorResponse() {
        super(409, "이미 닉네임 설정 및 신랑 신부 역할이 입력되었습니다.", null);
    }
}