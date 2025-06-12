package com.wedvice.security.login;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenExpiration;

    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject()); // sub에 userId가 들어있다고 가정
    }

    public String getNicknameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
        ((Map) claims).forEach((a, b) -> {
        });
        return claims.get("nickname", String.class); // nickname을 따로 넣은 경우
    }

    public String resolveToken(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");

        System.out.println("authorization = " + authorization);

        String accessToken = authorization.split(" ")[1];

        if (accessToken == null) return null;


        return accessToken;
    }

    public String generateAccessToken(String userId,String nickname, String oauthId) {
        return generateToken(userId, nickname,oauthId, accessTokenExpiration);
    }

    public String generateRefreshToken(String userId,String nickname, String oauthId) {
        return generateToken(userId,nickname, oauthId, refreshTokenExpiration);
    }

    private String generateToken(String userId, String nickname,String oauthId, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userId) // 내부 식별자 (PK)
                .claim("oauthId", oauthId)
                .claim("nickname",nickname)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (IllegalArgumentException | JwtException e) {
            return false;
        }
    }

    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}