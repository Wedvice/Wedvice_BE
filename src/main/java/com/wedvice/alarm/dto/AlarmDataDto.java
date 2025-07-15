package com.wedvice.alarm.dto;

import com.wedvice.alarm.type.RedirectTheme;
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
public class AlarmDataDto {

    @Schema(description = "알림 클릭 시 이동할 대상 구분값", example = "DETAIL")
    private RedirectTheme redirectTheme;

    @Schema(description = "관련 Task ID (redirectTheme가 DETAIL일 경우 사용)", example = "12", nullable = true)
    private Long taskId;

    @Schema(description = "관련 SubTask ID (redirectTheme가 SUBTASK일 경우 사용)", example = "34", nullable = true)
    private Long subTaskId;

    @Schema(description = "댓글 또는 메모 ID (redirectTheme가 COMMENT일 경우 사용)", example = "56", nullable = true)
    private Long commentId;

    @Schema(description = "캘린더에서 이동할 날짜 (redirectTheme가 CALENDAR일 경우 사용)", example = "2025-07-20", nullable = true)
    private LocalDate calendarDate;

    @Schema(description = "커플 ID (특정 알림에서 관계성 확인 시 사용 가능)", example = "-1", nullable = true)
    private Long coupleId;
}
