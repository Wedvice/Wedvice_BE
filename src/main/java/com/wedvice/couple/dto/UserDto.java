package com.wedvice.couple.dto;

import com.wedvice.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserDto {
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String imageUrl;

    @Schema(description = "사용자 메모", example = "신혼여행 계획 세우기")
    private String memo;

    @Schema(description = "현재 로그인한 사용자의 상대방 여부", example = "true")
    private boolean isPartner;

    public UserDto(String imageUrl, String memo, boolean isPartner) {
        this.imageUrl = imageUrl;
        this.memo = memo;
        this.isPartner = isPartner;
    }

    public static UserDto of(User user, Long currentUserId) {
        boolean isPartner = !user.getId().equals(currentUserId);
        return new UserDto(user.getProfileImageUrl(), user.getMemo(), isPartner);
    }
}
