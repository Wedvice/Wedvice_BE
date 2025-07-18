package com.wedvice.security.login;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/custom")
@Slf4j
public class OAuth2AuthorizationController {

    @GetMapping("/redirect-to-oauth")
    public void setRedirectUrlCookie(@RequestParam String redirectUrl,
        HttpServletResponse response) throws IOException {
        if (!isAllowedRedirectUrl(redirectUrl)) {
            throw new IllegalArgumentException("허용되지 않은 URL");
        }

        ResponseCookie cookie = ResponseCookie.from("loginRedirectUrl", redirectUrl)
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/")
            .maxAge(Duration.ofMinutes(3))
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
        log.info("[setRedirectUrlCookie] {}", cookie);
        response.sendRedirect("/oauth2/authorization/kakao"); // Spring Security가 처리하는 실제 경로
    }

    private boolean isAllowedRedirectUrl(String url) {
        return url.startsWith("http://localhost:3000") || url.startsWith("https://www.wedy.co.kr");
    }
}