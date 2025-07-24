package com.wedvice.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class DeleteTasksRequestDto {

    @NotEmpty(message = "Task ID 목록은 비어 있을 수 없습니다.")
    @Schema(description = "삭제할 Task ID 목록", example = "[1, 2, 3]", required = true)
    private List<Long> taskIds;
}
