package com.wedvice.subtask.dto;

import com.wedvice.subtask.entity.SubTask;
import com.wedvice.user.entity.User;
import com.wedvice.user.entity.User.Role;
import com.wedvice.user.entity.UserConfig.Color;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class CalenderSummaryResponseDto {

    @Schema(description = "역할 신부의 색깔")
    private Color groomColor;

    @Schema(description = "역할 신랑의 색깔")
    private Color brideColor;

    @Schema(description = "역할 함께의 색깔")
    private Color togetherColor;
    private List<SubTaskSummaryDto> subTaskSummaryDto;

    public static CalenderSummaryResponseDto of(User user, List<SubTask> subTaskList) {
        List<SubTaskSummaryDto> summaries = subTaskList.stream()
            .map(SubTaskSummaryDto::of)
            .toList();

        return CalenderSummaryResponseDto.builder()
            .groomColor(user.provideThatColor(Role.GROOM))
            .brideColor(user.provideThatColor(Role.BRIDE))
            .togetherColor(user.provideThatColor(Role.TOGETHER))
            .subTaskSummaryDto(summaries)
            .build();
    }
}
