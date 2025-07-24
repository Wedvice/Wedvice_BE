package com.wedvice.user.dto;

import com.wedvice.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyAccountResponseDto {

    private final String nickname;
    private final String email;
    private final String profileImageUrl;

    @Builder
    private MyAccountResponseDto(String nickname, String email, String profileImageUrl) {
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public static MyAccountResponseDto of(User user) {
        return MyAccountResponseDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
