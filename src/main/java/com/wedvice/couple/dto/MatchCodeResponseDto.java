package com.wedvice.couple.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "매치코드 응답 DTO")
public class MatchCodeResponseDto {
    @Schema(description = "매치코드 문자열", example = "무서운호랑이123")
    private String matchCode;
}
