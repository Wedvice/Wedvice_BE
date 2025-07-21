package com.wedvice.user.dto;

import com.wedvice.user.entity.UserConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "사용자 색상 설정 업데이트 요청 DTO")
public class UpdateColorConfigRequestDto {

    @Schema(description = "나의 색상", example = "BLUE")
    private UserConfig.Color myColor;

    @Schema(description = "파트너 색상", example = "RED")
    private UserConfig.Color partnerColor;
}
