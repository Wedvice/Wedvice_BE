package com.wedvice.controller;

import com.wedvice.service.UserService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/refresh")
    public ResponseEntity<Map> refresh(@CookieValue(name = "refreshToken", required = false) Cookie cookie) {
        Map<String, Object> result = userService.refresh(cookie);
        return ResponseEntity.ok(result);
    }
}
