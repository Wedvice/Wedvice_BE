package com.wedvice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedvice.security.login.ExceptionHandlingFilter;
import com.wedvice.security.login.JwtAuthenticationFilter;
import com.wedvice.security.login.JwtTokenProvider;
import com.wedvice.user.entity.User;
import com.wedvice.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionHandlingFilter exceptionHandlingFilter;

    public SecurityConfig(UserService userService, JwtTokenProvider jwtTokenProvider, JwtAuthenticationFilter jwtAuthenticationFilter, ExceptionHandlingFilter exceptionHandlingFilter) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.exceptionHandlingFilter = exceptionHandlingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlingFilter, JwtAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/css/**",
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

                            log.info("✅ 로그인 성공: " + oauth2User.getAttributes());

                            // ✅ 카카오 사용자 정보 추출
                            Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttributes().get("kakao_account");
                            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

                            String oauthId = oauth2User.getAttribute("id").toString();
                            String provider = "kakao";

                            String profileImageUrl = profile.get("profile_image_url") != null ? profile.get("profile_image_url").toString() : null;

                            // ✅ DB에 사용자 정보 저장 (이미 있으면 무시)
                            User user = userService.saveOrGetUser(oauthId, provider, profileImageUrl);
                            // ✅ JWT 생성
                            String accessToken = jwtTokenProvider.generateAccessToken(user.getId().toString(), user.getNickname(), user.getOauthId());
                            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString(), user.getNickname(), user.getOauthId());
                            user.updateRefreshToken(refreshToken);

                            // ✅ JWT를 HttpOnly 쿠키에 저장 (클라이언트 JS에서 접근 불가)
                            Cookie accessCookie = new Cookie("accessToken", accessToken);
                            accessCookie.setHttpOnly(false);
                            accessCookie.setPath("/");
                            accessCookie.setMaxAge(60 * 30); // 30분

                            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
                            refreshCookie.setHttpOnly(true);
                            refreshCookie.setPath("/");
                            refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 14일

                            response.addCookie(accessCookie);
                            response.addCookie(refreshCookie);

                            String host = request.getHeader("Host");
                            String referer = request.getHeader("Referer");

                            String redirectUrl;

                            if ((host != null && host.contains("localhost")) ||
                                    (referer != null && referer.contains("localhost"))) {
                                redirectUrl = "http://localhost:3000/Redirection";
                            } else {
                                redirectUrl = "https://www.wedy.co.kr/Redirection";
                            }
                            log.info("[success] host {}", host);
                            log.info("[success] referer {}", referer);
                            log.info("[success] redirectUrl {}", redirectUrl);
                            response.sendRedirect(redirectUrl);
                        })
                        .failureHandler((request, response, exception) -> {
                            log.info("❌ 로그인 실패: {}", exception.getLocalizedMessage());
                            String host = request.getHeader("Host");
                            String referer = request.getHeader("Referer");

                            String redirectUrl;

                            if ((host != null && host.contains("localhost")) ||
                                    (referer != null && referer.contains("localhost"))) {
                                redirectUrl = "http://localhost:3000/Redirection";
                            } else {
                                redirectUrl = "https://www.wedy.co.kr/Redirection";
                            }


                            log.info("[fail] host {}", host);
                            log.info("[fail] referer {}", referer);
                            log.info("[fail] redirectUrl {}", redirectUrl);
                            response.sendRedirect(redirectUrl);
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

                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://www.wedy.co.kr"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
