package com.wedvice.subtask.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CalenderSummaryResponseDto {

    @Schema(description = "목표일", example = "2024-12-20")
    private LocalDate targetDate;

    @Schema(description = "할일", example = "정장 업체 선정 및 예약")
    private String content;

    @Schema(description = "카테고리 정보", example = "드레스/정장")
    private String title;

    @Schema(description = "역할", example = "GROOM")
    private String role;

    @Schema(description = "서브테스크 id", example = "1")
    private Long id;
}
