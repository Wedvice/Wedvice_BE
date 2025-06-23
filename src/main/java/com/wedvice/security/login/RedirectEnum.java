package com.wedvice.security.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public enum RedirectEnum {

    JUST_USER(0, "매칭이 필요합니다"),
    NOT_COMPLETED(1, "필수 정보 입력이 필요합니다."),
    ONLY_COMPLETED(2, "상대방의 필수정보 입력이 필요합니다."),
    PAIR_COMPLETED(3, "입력이 완료되었습니다.");

    @Schema(description = "옮길 화면을 정의하는 id (ex. 0:코드 매치화면, 1:닉네임, 성별입력, 2:로딩화면, 3:홈페이지)")
    private final int number;
    @Schema(description = "설명이 담긴 필드")
    private final String message;

    RedirectEnum(int number, String message) {
        this.number = number;
        this.message = message;
    }
}
