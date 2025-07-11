package com.wedvice.subtask.dto;

import com.wedvice.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class HomeSubTaskConditionDto {

    private Long coupleId;
    private Boolean completed;
    private User.Role role;
    private String sort;
    private boolean top3;
    private Pageable pageable;
}
