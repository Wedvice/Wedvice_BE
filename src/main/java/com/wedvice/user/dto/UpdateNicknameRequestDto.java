package com.wedvice.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNicknameRequestDto {

    @Schema(description = "새로운 닉네임", example = "새닉네임")
    @NotBlank(message = "닉네임은 비어있을 수 없습니다.")
    @Size(min = 1, max = 2, message = "닉네임은 1자 이상 2자 이하여야 합니다.")
    private String newNickname;

    public UpdateNicknameRequestDto(String newNickname) {
        this.newNickname = newNickname;
    }
}
