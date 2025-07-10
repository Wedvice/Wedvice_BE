package com.wedvice.subtask.dto;

import com.wedvice.user.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(min = 1, max = 18)
    private String title;


    @NotNull(message = "역할은 필수값입니다.")
    private User.Role role;

    private String description;

    private LocalDate localDate;
}
