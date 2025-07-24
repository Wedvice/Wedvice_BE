package com.wedvice.user.dto;

import com.wedvice.user.entity.User;
import com.wedvice.user.entity.UserConfig;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PartnerImageAndColorResponseDto {

    private final String partnerNickname;
    private final String partnerProfileImageUrl;
    private final UserConfig.Color partnerColor;

    @Builder
    private PartnerImageAndColorResponseDto(String partnerNickname, String partnerProfileImageUrl, UserConfig.Color partnerColor) {
        this.partnerNickname = partnerNickname;
        this.partnerProfileImageUrl = partnerProfileImageUrl;
        this.partnerColor = partnerColor;
    }

    public static PartnerImageAndColorResponseDto of(User partner, UserConfig myUserConfig) {
        return PartnerImageAndColorResponseDto.builder()
                .partnerNickname(partner.getNickname())
                .partnerProfileImageUrl(partner.getProfileImageUrl())
                .partnerColor(myUserConfig.providePartnerColor())
                .build();
    }
}
