package com.wedvice.security.login;

import com.wedvice.common.ApiResponse;
import com.wedvice.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;


    //    필요한지?
    @GetMapping("/status")
    public Map<String, Object> loginStatus() {
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            response.put("isLoggedIn", true);
            response.put("user", oauth2User.getAttributes());
        } else {
            response.put("isLoggedIn", false);
        }
        return response;
    }

    @GetMapping("/redirect")
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
