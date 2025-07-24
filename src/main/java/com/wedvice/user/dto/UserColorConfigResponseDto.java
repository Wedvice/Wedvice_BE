package com.wedvice.user.dto;

import com.wedvice.user.entity.UserConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "사용자 색상 설정 응답 DTO")
public class UserColorConfigResponseDto {

    @Schema(description = "나의 색상", example = "BLUE")
    private UserConfig.Color myColor;

    @Schema(description = "파트너 색상", example = "RED")
    private UserConfig.Color partnerColor;

    @Schema(description = "우리 커플 색상", example = "GREEN")
    private UserConfig.Color ourColor;

    public static UserColorConfigResponseDto of(UserConfig userConfig) {
        return UserColorConfigResponseDto.builder()
            .myColor(userConfig.getMyColor())
            .partnerColor(userConfig.getPartnerColor())
            .ourColor(userConfig.getOurColor())
            .build();
    }
}
