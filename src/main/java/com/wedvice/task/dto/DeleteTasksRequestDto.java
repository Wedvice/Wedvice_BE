package com.wedvice.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@NoArgsConstructor
@Setter
public class DeleteTasksRequestDto {
    @Schema(description = "삭제할 Task ID 목록", example = "[1, 2, 3]")
    private List<Long> taskIds;
}
