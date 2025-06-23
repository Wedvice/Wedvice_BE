package com.wedvice.security.login;

import com.wedvice.common.ApiResponse;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import com.wedvice.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;


    /**
     1. 로그인 성공 후 백엔드에서 프론트에 /Redirection으로 보낸다
     2. 프론트에서는 그 후 /status로 요청을 보낸다
     3. /status경로는 security filter에서 permitall . 로그인이 안되어있으면 isLoggedIn : false로 반환값이 간다.
     4. 정상이면 여기서 화면 값을 받아간다.
     **/
    @GetMapping("/status")
    @Operation(
            summary = "로그인 후 리다이렉트 정보 조회",
            description = "현재 본인의 회원가입 진행 상태를 조회합니다."
    )
    public ResponseEntity<ApiResponse<RedirectResponseDto>> getLoginStatus(@LoginUser CustomUserDetails loginUser) {
        RedirectResponseDto redirectResponseDto = userService.getRedirectStatus(loginUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(redirectResponseDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refreshToken", required = false) Cookie cookie) {

        Map<String, Object> result = userService.refresh(cookie);
        HttpHeaders headers = (HttpHeaders) result.get("headers");
        Object body = result.get("body");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(body);
    }
}
