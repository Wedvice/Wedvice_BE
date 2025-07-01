package com.wedvice.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TestTokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
