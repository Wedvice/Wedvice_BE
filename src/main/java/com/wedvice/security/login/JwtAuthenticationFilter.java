package com.wedvice.security.login;

import com.wedvice.security.excption.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 🔥 인증 없이 접근 가능한 URL 목록
    private static final List<String> WHITE_LIST = List.of(
            "/",
            "/auth/refresh",
            "/auth/status",
            "/login/oauth2/code/kakao",
            "/oauth2/authorization/kakao",
            "/swagger-ui/index.html",
            "/swagger-ui/swagger-initializer.js",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui/index.css",
            "/swagger-ui/swagger-ui.css",
            "/v3/api-docs/swagger-config",
            "/v3/api-docs"


            // 추가로 공개 API 있으면 여기에 등록
    );

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {



        String requestURI = request.getRequestURI();
        if (!isWhiteListed(requestURI)) {

            String token = tokenProvider.resolveToken(request);

            // 화이트리스트가 아니면 JWT 검사
            System.out.println(requestURI);
            System.out.println(token);
            if (token == null || !tokenProvider.validateToken(token)) {
                throw new JwtAuthenticationException("Invalid or expired token");
            }
            System.out.println("통과함?");
            Long userId = tokenProvider.getUserIdFromToken(token);
            String username = tokenProvider.getNicknameFromToken(token);

            // 유저 정보를 담은 UserDetails 생성
            CustomUserDetails userDetails = new CustomUserDetails(userId, username, List.of());

            // Authentication 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            // SecurityContextHolder에 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }


    private boolean isWhiteListed(String uri) {
        // 단순히 시작 문자열 검사
        return WHITE_LIST.stream().anyMatch(uri::equals);
    }
}
