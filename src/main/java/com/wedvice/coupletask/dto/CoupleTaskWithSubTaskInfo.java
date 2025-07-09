package com.wedvice.coupletask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoupleTaskWithSubTaskInfo {
    private Long coupleTaskId;
    private String taskTitle;
    private Integer subTaskPrice;
}
