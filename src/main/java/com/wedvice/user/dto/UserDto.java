package com.wedvice.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String nickname;
    private String profileImageUrl;
    private String memo;
    private LocalDateTime createdAt;


    @QueryProjection
    public UserDto(Long id, String nickname, String profileImageUrl, String memo) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.memo = memo;
    }
}
