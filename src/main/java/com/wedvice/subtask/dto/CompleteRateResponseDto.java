package com.wedvice.subtask.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "완료율 응답 DTO")
public class CompleteRateResponseDto {

    @Schema(description = "완료 퍼센트 (0~100)", example = "54")
    private int percent;

    @Schema(description = "완료된 개수", example = "13")
    private long completed;

    @Schema(description = "전체 개수", example = "24")
    private long total;
}