package com.wedvice.controller.testuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class DetailUpdateRequestDto {

    @Schema(description = "테스트유저의 아이디", example = "1")
    private Long testUserId;
}
