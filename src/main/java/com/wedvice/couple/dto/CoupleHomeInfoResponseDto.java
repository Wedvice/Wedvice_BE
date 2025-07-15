package com.wedvice.couple.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CoupleHomeInfoResponseDto {

    @Schema(description = "신랑 정보")
    private UserDto groomDto;

    @Schema(description = "신부 정보")
    private UserDto brideDto;

    @Schema(description = "결혼 예정일", example = "2025-10-21")
    private LocalDate weddingDate;
}
