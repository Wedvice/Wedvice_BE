package com.wedvice.coupletask.dto;

import com.wedvice.coupletask.entity.CoupleTask;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CoupleTaskResponseDto {

    @NotNull
    @Schema(description = "커플테스크 id", example = "1")
    private Long coupleTaskId;
    @NotNull
    @Schema(description = "커플테스크의 이름 (ui에 표시할 문자열)", example = "스튜디오 촬영하기")
    private String title;

    public static CoupleTaskResponseDto from(CoupleTask coupleTask) {
        return CoupleTaskResponseDto.builder()
            .coupleTaskId(coupleTask.getId())
            .title(coupleTask.getTask().getTitle())
            .build();
    }
}
