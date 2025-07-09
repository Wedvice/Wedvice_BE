package com.wedvice.user.service;

import com.wedvice.couple.exception.InvalidUserAccessException;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.security.login.JwtTokenProvider;
import com.wedvice.security.login.RedirectResponseDto;
import com.wedvice.user.dto.MemoRequestDto;
import com.wedvice.user.dto.UserDto;
import com.wedvice.user.entity.User;
import com.wedvice.user.exception.TokenInvalidException;
import com.wedvice.user.exception.TokenMismatchException;
import com.wedvice.user.exception.TokenNotFoundException;
import com.wedvice.user.exception.UnknownTokenException;
import com.wedvice.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User saveOrGetUser(String oauthId, String provider, String profileImageUrl) {
        return userRepository.findByOauthId(oauthId)
            .orElseGet(() -> {
                User newUser = User.create(oauthId, provider);
                newUser.updateProfileImage(profileImageUrl);
                return userRepository.save(newUser);
            });
    }


    @Transactional
    public Map<String, Object> refresh(Cookie cookie) {
        if (cookie == null) {
            throw new TokenNotFoundException();
        }

        String refreshToken = cookie.getValue();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenInvalidException();
        }

        try {
            jwtTokenProvider.validateToken(refreshToken);

            String userId = jwtTokenProvider.getUserId(refreshToken);
            User user = userRepository.findById(Long.parseLong(userId)).orElseThrow();

            String savedRefreshToken = user.getRefreshToken();
            if (!savedRefreshToken.equals(refreshToken)) {
                throw new TokenMismatchException();
            }

            String newAccessToken = jwtTokenProvider.generateAccessToken(
                String.valueOf(user.getId()), user.getNickname(),
                String.valueOf(user.getOauthId()));
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(
                String.valueOf(user.getId()), user.getNickname(),
                String.valueOf(user.getOauthId()));

            user.updateRefreshToken(newRefreshToken);

            return createTokenResult(newAccessToken, newRefreshToken);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new UnknownTokenException();
        }
    }

    public Map<String, Object> createTokenResult(String accessToken, String refreshToken) {
        Map<String, Object> result = new HashMap<>();

        HttpHeaders headers = createTokenHeader(refreshToken);
        headers.add("Authorization", "Bearer " + accessToken); // 🔥 accessToken 헤더에 추가

        result.put("headers", headers);

        return result;
    }

    public HttpHeaders createTokenHeader(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie",
            createTokenCookie("refreshToken", refreshToken, 60 * 60 * 24 * 7));
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


    @Transactional(readOnly = true)
    public UserDto getUserInfo(Long userId) {
        User user = userRepository.getReferenceById(userId);
        return UserDto.builder()
            .id(user.getId())
            .memo(user.getMemo())
            .nickname(user.getNickname())
            .profileImageUrl(user.getProfileImageUrl())
            .createdAt(user.getCreatedAt())
            .build();
    }

    public List<UserDto> getAllUserTestExample() {
        return userRepository.getAllUserTestExample();
    }

    @Transactional(readOnly = true)
    public RedirectResponseDto getRedirectStatus(Long userId) {
        // 반복되는 구문, UserReader 컴포넌트 고려
        User user = userRepository.findByUserWithCoupleAndPartner(userId)
            .orElseThrow(InvalidUserAccessException::new);

        return RedirectResponseDto.from(user.getMatchingStatus());
    }

    @Transactional
    public void updateMemo(Long userId, MemoRequestDto requestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(InvalidUserAccessException::new);

        user.updateMemo(requestDto.getMemo());
    }
}


