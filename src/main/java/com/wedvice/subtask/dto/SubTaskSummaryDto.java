package com.wedvice.subtask.dto;

import com.wedvice.subtask.entity.SubTask;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class SubTaskSummaryDto {

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

    public static SubTaskSummaryDto of(SubTask subTask) {
        return SubTaskSummaryDto.builder()
            .targetDate(subTask.getTargetDate())
            .content(subTask.getContent())
            .title(subTask.getCoupleTask().getTask().getTitle())
            .role(String.valueOf(subTask.getRole()))
            .id(subTask.getId())
            .build();
    }
}
