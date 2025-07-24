package com.wedvice.couple.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Schema(description = "결혼 예정일 수정 요청 DTO")
public class UpdateWeddingDateRequestDto {

    @NotNull(message = "결혼 예정일은 필수입니다.")
    @FutureOrPresent(message = "결혼 예정일은 오늘 날짜 이후여야 합니다.")
    @Schema(description = "수정할 결혼 예정일", example = "2025-12-25")
    private LocalDate weddingDate;
}
