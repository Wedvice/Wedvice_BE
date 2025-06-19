package com.wedvice.security.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RedirectResponseDto {
    @Schema(description = "옮길 화면을 정의하는 id (ex. 0:코드 매치화면, 1:닉네임, 성별입력, 2:로딩화면, 3:홈페이지)")
    private int redirectCode;

    @Schema(description = "간단한 설명 메시지")
    private String redirectMessage;

    public static RedirectResponseDto from(RedirectEnum redirectEnum) {
        return new RedirectResponseDto(redirectEnum.getNumber(), redirectEnum.getMessage());
    }
}
