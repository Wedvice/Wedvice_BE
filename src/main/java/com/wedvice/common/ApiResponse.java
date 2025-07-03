package com.wedvice.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "공통 응답 포멧")
public class ApiResponse<T> {
    @Schema(description = "응답 코드", example = "200")
    private int code;
    @Schema(description = "응답 코드 설명", example = "성공")
    private String message;
    @Schema(description = "데이터 스키마", example = "Json형식의 DTO")
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "성공", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}