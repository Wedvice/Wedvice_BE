package com.wedvice.alarm.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AlarmResponseDto {

    @Schema(description = "알림 ID", example = "1")
    private Long id;

    @Schema(description = "알림 유형 (AlarmType의 name)", example = "TASK_CREATED")
    private String type;

    @Schema(description = "완성된 알림 메시지", example = "신부님이 새로운 리스트를 등록했어요.")
    private String message;

    @Schema(description = "알림 읽음 여부", example = "false")
    private boolean isRead;

    @Schema(description = "알림 생성 시각 (yyyy-MM-dd HH:mm 형식)", example = "2025-07-15 13:45")
    private String createdAt;

    @Schema(description = "알림 메시지에 사용된 원시 데이터", nullable = true)
    private AlarmParamDto params;

    @Schema(description = "알림 클릭 시 이동할 목적지 정보", nullable = true)
    private AlarmDataDto data;
}
