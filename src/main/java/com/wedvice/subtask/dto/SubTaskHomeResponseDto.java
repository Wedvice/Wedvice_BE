package com.wedvice.subtask.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "홈 화면에 표시되는 서브태스크 요약 DTO")
public class SubTaskHomeResponseDto {

    @Schema(description = "SubTask ID", example = "100")
    private Long subTaskId;

    @Schema(description = "SubTask ID", example = "100")
    private String subTaskContent;

    @Schema(description = "CoupleTask ID", example = "25")
    private Long coupleTaskId;

    @Schema(description = "taskContent", example = "스튜디오 촬영하기")
    private String taskContent;

    @Schema(description = "예정일", example = "2025-07-01")
    private LocalDate targetDate;

    @Schema(description = "완료 여부", example = "false")
    private boolean completed;

    @Schema(description = "정렬용 인덱스 (카테고리 내 순서)", example = "0")
    private int orders;
}