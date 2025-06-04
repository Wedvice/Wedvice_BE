package com.wedvice.service;

import com.wedvice.dto.TokenResponseDto;
import com.wedvice.dto.UserDto;
import com.wedvice.entity.Couple;
import com.wedvice.entity.User;
import com.wedvice.repository.CoupleRepository;
import com.wedvice.repository.UserRepository;
import com.wedvice.security.login.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User saveOrGetUser(String oauthId, String provider, String nickname, String profileImageUrl) {
        return userRepository.findByOauthIdAndProvider(oauthId, provider)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .oauthId(oauthId)
                            .provider(provider)
                            .nickname(nickname)
                            .profileImageUrl(profileImageUrl)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    @Transactional
    public void updateUserInfo(Long userId, String nickname, Long matchedUserId, String memo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (nickname != null) user.setNickname(nickname);
        if (matchedUserId != null) user.setMatchedUserId(matchedUserId);
        if (memo != null) user.setMemo(memo);

        userRepository.save(user);
    }

    @Transactional
    public void updateMemo(Long userId, String memo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setMemo(memo);
        userRepository.save(user);
    }

    @Transactional
    public void updateMatchedUserAndCreateCouple(Long userId, Long matchedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        User matchedUser = userRepository.findById(matchedUserId)
                .orElseThrow(() -> new RuntimeException("매칭된 사용자를 찾을 수 없습니다."));

        // ✅ User의 matchedUserId 양방향 업데이트
        user.setMatchedUserId(matchedUserId);
        matchedUser.setMatchedUserId(userId);
        userRepository.save(user);
        userRepository.save(matchedUser);

        // ✅ 이미 존재하는 커플인지 확인

        boolean coupleExists = coupleRepository.existsByGroomAndBride(user, matchedUser) ||
                coupleRepository.existsByGroomAndBride(matchedUser, user);

        if (!coupleExists) {
            // ✅ Couple 테이블에 새로운 로우 생성
            Couple couple = Couple.builder()
                    .groom(user)
                    .bride(matchedUser)
                    .build();
            coupleRepository.save(couple);
        }
    }

    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public Map<String, Object> refresh(Cookie cookie) {
        if (cookie == null) {
            throw new IllegalArgumentException("쿠키가 존재하지 않음");
        }

        String refreshToken = cookie.getValue();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("리프래쉬 토큰이 없음");
        }

        try {
            jwtTokenProvider.validateToken(refreshToken);

            String uid = jwtTokenProvider.getUserId(refreshToken);
            User user = userRepository.getReferenceById(Long.parseLong(uid));

            String savedRefreshToken = user.getRefreshToken();
            if (!savedRefreshToken.equals(refreshToken)) {
                throw new IllegalArgumentException("리프래쉬 토큰이 일치하지 않습니다.");
            }

            String newAccessToken = jwtTokenProvider.generateAccessToken(
                    String.valueOf(user.getId()), String.valueOf(user.getOauthId()));
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(
                    String.valueOf(user.getId()), String.valueOf(user.getOauthId()));

            userRepository.updateRefreshToken(user.getId(), newRefreshToken);

            return createTokenResult(newAccessToken, newRefreshToken);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("알 수 없는 에러가 발생했습니다.");
        }
    }

    public Map<String, Object> createTokenResult(String accessToken, String refreshToken) {
        Map<String, Object> result = new HashMap<>();

        HttpHeaders headers = createTokenHeader(refreshToken);
        TokenResponseDto responseDto = TokenResponseDto.builder()
                .accessToken("Bearer " + accessToken)
                .build();

        result.put("headers", headers);
        result.put("body", responseDto);

        return result;
    }

    public HttpHeaders createTokenHeader(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", createTokenCookie("refreshToken", refreshToken, 60 * 60 * 24 * 7));
        return headers;
    }

    public HttpHeaders deleteTokenHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", createTokenCookie("refreshToken", "", 0));
        return headers;
    }

    public String createTokenCookie(String name, String token, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, token)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(maxAge)
                .build();
        return cookie.toString();
    }

    @Transactional
    public void touchRefreshToken(String refreshToken, Long id) {
        userRepository.updateRefreshToken(id, refreshToken);
    }

    public UserDto getUserInfo(Long userId) {
        User user = userRepository.getReferenceById(userId);
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .memo(user.getMemo())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .build();
        return userDto;
    }
}


