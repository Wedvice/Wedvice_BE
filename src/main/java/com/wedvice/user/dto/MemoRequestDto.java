package com.wedvice.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemoRequestDto {
    @Schema(description = "수정할 메모", example = "고마워 😊")
    private String memo;
}
