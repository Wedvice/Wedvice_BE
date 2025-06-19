package com.wedvice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedvice.security.login.ExceptionHandlingFilter;
import com.wedvice.security.login.JwtAuthenticationFilter;
import com.wedvice.security.login.JwtTokenProvider;
import com.wedvice.user.entity.User;
import com.wedvice.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
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
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlingFilter, JwtAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login/**", "/oauth2/**", "/login/oauth2/**", "/css/**", "/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
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
                                    User user = userService.saveOrGetUser(oauthId, provider, nickname, profileImageUrl);
                                    // ✅ JWT 생성
                                    String accessToken = jwtTokenProvider.generateAccessToken(user.getId().toString(), user.getNickname(), user.getOauthId());
                                    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString(), user.getNickname(), user.getOauthId());
                                    userService.touchRefreshToken(refreshToken, user.getId());

//                            프론트에서 가지고있는 엑세스 토큰이 백엔드로 전달되었을 경우
//                            1. 결과값 응답
//                            2. 만료된 토큰일 경우 /refresh경로 타라고 말해줘야함 -> 어떤 에러코드일 경우에 백엔드한테 /refresh경로로 다시 보내라
//                            3. 아예 잘못된 토큰일 경우

//                            엑세스 토큰 받으면 Authrorization Bearer + //
                                    // ✅ JWT를 HttpOnly 쿠키에 저장 (클라이언트 JS에서 접근 불가)
                                    Cookie accessCookie = new Cookie("accessToken", accessToken);
                                    accessCookie.setHttpOnly(true);
                                    accessCookie.setPath("/");
                                    accessCookie.setMaxAge(60 * 30); // 30분

                                    Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
                                    refreshCookie.setHttpOnly(true);
                                    refreshCookie.setPath("/");
                                    refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 14일

                                    response.addCookie(accessCookie);
                                    response.addCookie(refreshCookie);


                                    // ✅ React 대시보드로 리디렉트
                                    response.sendRedirect("http://localhost:3000/Redirection");
                                })
                                .failureHandler((request, response, exception) -> {
                                    System.out.println("❌ 로그인 실패: " + exception.getLocalizedMessage());
                                    response.sendRedirect("http://localhost:3000/Redirection");
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


}
