package com.wedvice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedvice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:5173"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                            System.out.println("✅ 로그인 성공: " + oauth2User.getAttributes());

                            // ✅ 카카오 사용자 정보 추출
                            Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttributes().get("kakao_account");
                            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

                            String oauthId = oauth2User.getAttribute("id").toString();
                            String provider = "kakao";

                            String nickname = profile.get("nickname") != null ? profile.get("nickname").toString() : null;
                            String profileImageUrl = profile.get("profile_image_url") != null ? profile.get("profile_image_url").toString() : null;

                            // ✅ DB에 사용자 정보 저장 (이미 있으면 무시)
                            userService.saveOrGetUser(oauthId, provider, nickname, profileImageUrl);

                            // ✅ React 대시보드로 리디렉트
                            response.sendRedirect("http://localhost:5173/dashboard");
                        })
                        .failureHandler((request, response, exception) -> {
                            System.out.println("❌ 로그인 실패: " + exception.getLocalizedMessage());
                            response.sendRedirect("http://localhost:5173/login");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            Map<String, Object> result = new HashMap<>();
                            result.put("message", "로그아웃 성공");
                            result.put("isLoggedIn", false);
                            new ObjectMapper().writeValue(response.getWriter(), result);
                        })
                );

        return http.build();
    }
}
