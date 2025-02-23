package com.wedvice.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String loginPage() {
        return "<h1>카카오 로그인 테스트</h1><a href='/oauth2/authorization/kakao'>카카오 로그인</a>";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User oauth2User) {
        return "✅ 카카오 로그인 성공! 🎉 사용자 정보: " + oauth2User.getAttributes();
    }

    @GetMapping("/login")
    public String login() {
        System.out.println("123");
        return "<h1>로그인 페이지</h1><a href='/oauth2/authorization/kakao'>카카오 로그인</a>";
    }
}
