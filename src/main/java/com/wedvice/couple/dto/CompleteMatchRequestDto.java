package com.wedvice.couple.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CompleteMatchRequestDto {
    @NotNull
    private Gender gender;
    @NotNull
    private String nickName;
}
