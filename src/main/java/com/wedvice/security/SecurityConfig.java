package com.wedvice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedvice.security.login.ExceptionHandlingFilter;
import com.wedvice.security.login.JwtAuthenticationFilter;
import com.wedvice.security.login.JwtTokenProvider;
import com.wedvice.user.entity.User;
import com.wedvice.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final ExceptionHandlingFilter exceptionHandlingFilter;
  private final ClientRegistrationRepository clientRegistrationRepository;

  public SecurityConfig(UserService userService, JwtTokenProvider jwtTokenProvider,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      ExceptionHandlingFilter exceptionHandlingFilter,
      ClientRegistrationRepository clientRegistrationRepository) {
    this.userService = userService;
    this.jwtTokenProvider = jwtTokenProvider;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.exceptionHandlingFilter = exceptionHandlingFilter;
    this.clientRegistrationRepository = clientRegistrationRepository;
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
                "/custom/redirect-to-oauth",
                "/oauth2/**",
                "/login/oauth2/**",
                "/css/**",
                "/auth/**",
                "/swagger-ui/**",
                "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
        )

        .oauth2Login(oauth2 -> oauth2
                .loginPage("/custom/redirect-to-oauth")
                .authorizationEndpoint(auth -> auth.baseUri("/oauth2/authorization"))
                .successHandler((request, response, authentication) -> {
                  OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                  log.info("✅ 로그인 성공: " + oauth2User.getAttributes());

                  // ✅ 카카오 사용자 정보 추출
                  Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttributes()
                      .get("kakao_account");
                  Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

                  String oauthId = oauth2User.getAttribute("id").toString();
                  String provider = "kakao";

                  String profileImageUrl =
                      profile.get("profile_image_url") != null ? profile.get("profile_image_url")
                          .toString() : null;

                  // ✅ DB에 사용자 정보 저장 (이미 있으면 무시)
                  User user = userService.saveOrGetUser(oauthId, provider, profileImageUrl);
                  // ✅ JWT 생성
                  String accessToken = jwtTokenProvider.generateAccessToken(user.getId().toString(),
                      user.getNickname(), user.getOauthId());
                  String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString(),
                      user.getNickname(), user.getOauthId());
                  user.updateRefreshToken(refreshToken);

                  // ✅ 리다이렉션 URL 쿠키에서 추출
                  String redirectUrl = "https://www.wedy.co.kr"; // fallback
                  boolean isLocalhost = false;
                  Cookie[] cookies = request.getCookies();
                  if (cookies != null) {
                    for (Cookie cookie : cookies) {
                      if ("loginRedirectUrl".equals(cookie.getName())) {
                        redirectUrl = cookie.getValue();
                        if (redirectUrl.contains("localhost")) {
                          log.info("[successHandler 쿠키] {} {}", cookie.getName(), cookie.getValue());
                          isLocalhost = true;
                        }
                        break;
                      }
                    }
                  }
                  // ✅ 리다이렉션 쿠키 삭제
                  ResponseCookie deleteCookie = ResponseCookie.from("loginRedirectUrl", "")
                      .path("/")
                      .httpOnly(true)
                      .secure(true)
                      .sameSite("Lax")
                      .maxAge(0)
                      .build();
                  response.addHeader("Set-Cookie", deleteCookie.toString());

                  ResponseCookie.ResponseCookieBuilder accessTokenCookieBuilder = ResponseCookie.from(
                          "accessToken", accessToken)
                      .path("/")
                      .maxAge(Duration.ofMinutes(5))
                      .httpOnly(false) // FE가 읽어야 하므로 false
                      .secure(!isLocalhost); // HTTPS 환경에서만 Secure 적용

// ✅ SameSite 설정은 로컬에서는 제거
                  if (!isLocalhost) {
                    accessTokenCookieBuilder.sameSite("None").domain("wedy.co.kr");
                  }

                  ResponseCookie accessTokenCookie = accessTokenCookieBuilder.build();

                  // ✅ refreshToken 쿠키 설정
                  ResponseCookie.ResponseCookieBuilder refreshTokenCookieBuilder = ResponseCookie.from(
                          "refreshToken", refreshToken)
                      .path("/")
                      .maxAge(Duration.ofDays(14))
                      .httpOnly(true) // FE에서 접근 못하게
                      .sameSite(isLocalhost ? "Lax" : "None")
                      .secure(!isLocalhost);

                  if (!isLocalhost) {
                    refreshTokenCookieBuilder.domain("wedy.co.kr");
                  }
                  ResponseCookie refreshTokenCookie = refreshTokenCookieBuilder.build();

                  response.addHeader("Set-Cookie", accessTokenCookie.toString());
                  response.addHeader("Set-Cookie", refreshTokenCookie.toString());

                  // ✅ 리다이렉트 처리
                  String host = request.getHeader("Host");

                  log.info("[success] host {}", host);
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
