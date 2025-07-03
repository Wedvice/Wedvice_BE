package com.wedvice.controller;

import com.wedvice.common.ApiResponse;
import com.wedvice.controller.dto.TestTokenResponseDto;
import com.wedvice.security.login.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("local") // 또는 직접 env 체크
@RestController
@RequiredArgsConstructor
@RequestMapping("/test-auth")
@Tag(name = "테스트를 위한 API", description = "테스트유저 토큰 발급 및 기타 사용")
public class TestAuthController {

  private final JwtTokenProvider jwtTokenProvider;

  @GetMapping("/token/{id}")
  public ResponseEntity<ApiResponse<TestTokenResponseDto>> getTestToken(
      @Parameter(name = "id", description = "-1 or -2", required = true) @PathVariable("id") String id) {

    return ResponseEntity.ok(
        ApiResponse.success(
            new TestTokenResponseDto(jwtTokenProvider.generateTestAccessToken(id, "test", "test"),
                jwtTokenProvider.generateTestRefreshToken(id, "test", "test"))));
  }
}
