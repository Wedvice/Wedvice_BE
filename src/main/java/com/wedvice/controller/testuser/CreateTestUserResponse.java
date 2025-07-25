package com.wedvice.controller.testuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CreateTestUserResponse {

    @Schema(description = "테스트 유저의 매치코드", example = "무서운호랑이123")
    private String matchCode;

    @Schema(description = "테스트 유저의 id", example = "1")
    private Long testUserId;

    public static CreateTestUserResponse from(String matchCode, Long testUserId) {
        return CreateTestUserResponse.builder()
            .matchCode(matchCode)
            .testUserId(testUserId)
            .build();
    }
}
