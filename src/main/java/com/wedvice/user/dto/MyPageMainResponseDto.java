package com.wedvice.user.dto;

import com.wedvice.couple.exception.NotMatchedYetException;
import com.wedvice.user.entity.User;
import com.wedvice.user.entity.UserConfig;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
@Builder
public class MyPageMainResponseDto {

    private String profileImageUrl;

    private String myNickName;

    private Long partnerId;

    private String partnerNickName;

    private LocalDateTime weddingDate;

    public static MyPageMainResponseDto of(User user, User partner, UserConfig userConfig) {
        return MyPageMainResponseDto.builder()
            .profileImageUrl(user.getProfileImageUrl())
            .myNickName(user.getNickname())
            .partnerId(partner != null ? partner.getId() : null)
            .partnerNickName(partner != null ? partner.getNickname() : null)
            .weddingDate(userConfig != null ? userConfig.getWeddingDate() : null)
            .build();
    }
}
