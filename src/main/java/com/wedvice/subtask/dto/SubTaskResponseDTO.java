package com.wedvice.subtask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubTaskResponseDTO {
    private Long coupleTaskId;
    private Long subTaskId;
    private String displayName;
    private Boolean completed;
    private String role;
    private Integer price;
    private LocalDate targetDate;
    private LocalDate completedDate;
    private String contents;
    private Integer orders;
}
