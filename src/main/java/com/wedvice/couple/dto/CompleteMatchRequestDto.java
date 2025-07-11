package com.wedvice.couple.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CompleteMatchRequestDto {

    @NotNull
    private Gender gender;
    @NotNull
    private String nickName;

    // 기본 생성자 (Jackson 등 직렬화용)
    protected CompleteMatchRequestDto() {
    }

    // 명시적 생성자 (테스트 및 수동 생성용)
    public CompleteMatchRequestDto(String nickName, Gender gender) {
        this.nickName = nickName;
        this.gender = gender;
    }
}
