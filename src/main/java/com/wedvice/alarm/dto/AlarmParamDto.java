package com.wedvice.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AlarmParamDto {

    @Schema(description = "알림과 관련된 상대방 닉네임", example = "준형")
    private String partnerNickname;

    @Schema(description = "알림에 포함된 커플 Task 제목", example = "예식장 예약")
    private String taskTitle;

    @Schema(description = "서브태스크(개별 할 일)의 내용", example = "드레스샵 방문 일정 조율")
    private String subTaskContent;

    @Schema(description = "댓글 또는 메모 등 미리보기용 텍스트", example = "이 디자인으로 확정할게요!")
    private String contentPreview;

    @Schema(description = "댓글 또는 메모 작성자의 닉네임", example = "신랑")
    private String creatorNickname;

    @Schema(description = "캘린더 제목", example = "웨딩 촬영일")
    private String calendarTitle;

    @Schema(description = "날짜 정보 (서브태스크/캘린더 등)", example = "2025-07-10")
    private LocalDate targetDate;
}