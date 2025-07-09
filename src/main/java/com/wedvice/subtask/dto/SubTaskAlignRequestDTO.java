package com.wedvice.subtask.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubTaskAlignRequestDTO {
    private List<Long> subTaskIds;
}
