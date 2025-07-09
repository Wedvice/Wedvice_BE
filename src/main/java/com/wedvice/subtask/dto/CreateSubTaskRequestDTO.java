package com.wedvice.subtask.dto;

import com.wedvice.user.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSubTaskRequestDTO {

    private Long coupleTaskId;

    private String title;

    private User.Role role;

    private String description;

    private LocalDate localDate;
}
