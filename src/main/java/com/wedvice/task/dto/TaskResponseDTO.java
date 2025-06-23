package com.wedvice.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {

    private Long taskId;
    String taskTitle;
    Long totalCount;
    Integer completedCount;
}
