package com.wedvice.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @Size(min = 2 , max = 10)
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
