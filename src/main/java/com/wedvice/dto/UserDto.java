package com.wedvice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserDto {
    private Long id;
    private String nickname;
    private String profileImageUrl;
    private String memo;
    private LocalDateTime createdAt;
}
