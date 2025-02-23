package com.wedvice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
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
                        .requestMatchers("/auth/**").permitAll() // 인증 필요 없는 경로
                        .anyRequest().authenticated()           // 그 외는 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            // ✅ 로그인 성공 시 React 페이지로 리디렉트
                            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                            System.out.println("✅ 로그인 성공: " + oauth2User.getAttributes());

                            response.sendRedirect("http://localhost:5173/dashboard");  // ✅ React 대시보드로 리디렉트
                        })
                        .failureHandler((request, response, exception) -> {
                            // ❌ 로그인 실패 시 React 로그인 페이지로 리디렉트
                            System.out.println("❌ 로그인 실패: " + exception.getLocalizedMessage());

                            response.sendRedirect("http://localhost:5173/login");  // ❌ React 로그인 페이지로 리디렉트
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
                            result.put("isLoggedIn", false); // ✅ 클라이언트와 맞춤
                            new ObjectMapper().writeValue(response.getWriter(), result);
                        })
                );

        return http.build();
    }
}
