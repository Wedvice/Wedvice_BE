package com.wedvice.controller;

import com.wedvice.common.ApiResponse;
import com.wedvice.common.swagger.DocumentedApiError;
import com.wedvice.controller.dto.TestTokenResponseDto;
import com.wedvice.controller.testuser.CreateTestUserResponse;
import com.wedvice.controller.testuser.DetailUpdateRequestDto;
import com.wedvice.controller.testuser.TestUserNotMatchedCreatedUserException;
import com.wedvice.controller.testuser.TestUserService;
import com.wedvice.couple.exception.InvalidUserAccessException;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.JwtTokenProvider;
import com.wedvice.security.login.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("local") // 또는 직접 env 체크
@RestController
@RequiredArgsConstructor
@RequestMapping("/test-auth")
@Tag(name = "테스트를 위한 API", description = "테스트유저 토큰 발급 및 기타 사용")
public class TestAuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final TestUserService userService;

    @GetMapping("/token/{id}")
    public ResponseEntity<ApiResponse<TestTokenResponseDto>> getTestToken(
        @Parameter(name = "id", description = "-1 or -2", required = true) @PathVariable("id") String id) {

        return ResponseEntity.ok(
            ApiResponse.success(
                new TestTokenResponseDto(
                    jwtTokenProvider.generateTestAccessToken(id, "test", "test"),
                    jwtTokenProvider.generateTestRefreshToken(id, "test", "test"))));
    }

    @PostMapping("/test-user")
    @Operation(summary = "테스트 유저 생성 및 매치코드 반환",
        description = "테스트 유저를 생성하고 세부 정보까지 입력시킨다")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<ApiResponse<CreateTestUserResponse>> createTestUser(
        @LoginUser CustomUserDetails loginUser) {
        CreateTestUserResponse response = userService.createTestUser(loginUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/test-user/detail")
    @Operation(summary = "테스트 유저의 상세 정보 입력",
        description = "테스트 유저의 상세 정보를 나의 반대 성별과 '테스'라는 닉네임으로 생성한다.")
    @DocumentedApiError(InvalidUserAccessException.class)
    @DocumentedApiError(TestUserNotMatchedCreatedUserException.class)
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<ApiResponse<?>> updateDetail(@LoginUser CustomUserDetails loginUser
        , @RequestBody DetailUpdateRequestDto requestDto) {
        userService.updateDetail(requestDto.getTestUserId(), loginUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/force-cleanup")
    @Operation(summary = "테스트 유저 강제 삭제")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Void> cleanupNow(@LoginUser CustomUserDetails loginUser) {
        userService.deleteUnusedTestUsers();
        return ResponseEntity.ok().build();
    }
}
