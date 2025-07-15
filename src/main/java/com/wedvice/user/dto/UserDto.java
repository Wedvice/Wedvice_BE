package com.wedvice.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @Size(max = 2)
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
