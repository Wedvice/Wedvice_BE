package com.wedvice.task.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@NoArgsConstructor
@Setter
public class DeleteTasksRequestDto {
    private List<Long> taskIds;
}
