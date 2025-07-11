package com.wedvice.user.service;

import com.wedvice.security.login.JwtTokenProvider;
import static org.assertj.core.api.Assertions.assertThat;

import com.wedvice.user.entity.User;
import com.wedvice.user.exception.TokenInvalidException;
import com.wedvice.user.exception.TokenMismatchException;
import com.wedvice.user.exception.TokenNotFoundException;
import com.wedvice.user.exception.UnknownTokenException;
import com.wedvice.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("saveOrGetUser: 사용자가 존재하지 않으면 새로 저장하고 반환한다.")
    void saveOrGetUser_UserDoesNotExist_SavesAndReturnsNewUser() {
        // Given
        String oauthId = "new-oauth-id";
        String provider = "kakao";
        String profileImageUrl = "http://new.profile.image/url.jpg";

        // Mocking: userRepository.findByOauthId(oauthId)가 Optional.empty()를 반환하도록 설정
        when(userRepository.findByOauthId(oauthId)).thenReturn(Optional.empty());

        // Mocking: userRepository.save(any(User.class))가 저장된 User 객체를 반환하도록 설정
        // User.create()는 내부적으로 호출되므로, save()가 반환할 객체를 미리 정의
        User newUser = User.create(oauthId, provider);
        newUser.updateProfileImage(profileImageUrl);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User resultUser = userService.saveOrGetUser(oauthId, provider, profileImageUrl);

        // Then
        assertThat(resultUser).isNotNull();
        assertThat(resultUser.getOauthId()).isEqualTo(oauthId);
        assertThat(resultUser.getProvider()).isEqualTo(provider);
        assertThat(resultUser.getProfileImageUrl()).isEqualTo(profileImageUrl);
        verify(userRepository, times(1)).findByOauthId(oauthId); // findByOauthId가 1번 호출되었는지 검증
        verify(userRepository, times(1)).save(any(User.class)); // save가 1번 호출되었는지 검증
    }

    @Test
    @DisplayName("saveOrGetUser: 사용자가 존재하면 기존 사용자를 반환하고 저장하지 않는다.")
    void saveOrGetUser_UserExists_ReturnsExistingUserAndDoesNotSave() {
        // Given
        String oauthId = "existing-oauth-id";
        String provider = "google";
        String profileImageUrl = "http://existing.profile.image/url.jpg";

        User existingUser = User.create(oauthId, provider);
        existingUser.updateProfileImage(profileImageUrl);

        // Mocking: userRepository.findByOauthId(oauthId)가 기존 User 객체를 반환하도록 설정
        when(userRepository.findByOauthId(oauthId)).thenReturn(Optional.of(existingUser));

        // When
        User resultUser = userService.saveOrGetUser(oauthId, provider, profileImageUrl);

        // Then
        assertThat(resultUser).isNotNull();
        assertThat(resultUser.getOauthId()).isEqualTo(oauthId);
        assertThat(resultUser.getProvider()).isEqualTo(provider);
        assertThat(resultUser.getProfileImageUrl()).isEqualTo(profileImageUrl);
        verify(userRepository, times(1)).findByOauthId(oauthId); // findByOauthId가 1번 호출되었는지 검증
        verify(userRepository, never()).save(any(User.class)); // save가 호출되지 않았는지 검증
    }

    @Test
    @DisplayName("refresh: 유효한 리프레시 토큰으로 새로운 액세스/리프레시 토큰을 발급받는다.")
    void refresh_ValidRefreshToken_ReturnsNewTokens() {
        // Given
        String refreshToken = "validRefreshToken";
        Long userId = 1L;
        String nickname = "testUser";
        String oauthId = "testOauthId";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        User user = User.createForTestWithId(userId, oauthId, "kakao", nickname, null); // 테스트용 User 생성
        user.updateRefreshToken(refreshToken); // User에 리프레시 토큰 설정

        // Mocking
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true); // 토큰 유효성 검사 통과
        when(jwtTokenProvider.getUserId(refreshToken)).thenReturn(String.valueOf(userId)); // userId 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user)); // User 조회 성공
        when(jwtTokenProvider.generateAccessToken(String.valueOf(userId), nickname, oauthId)).thenReturn(newAccessToken); // 새 액세스 토큰 생성
        when(jwtTokenProvider.generateRefreshToken(String.valueOf(userId), nickname, oauthId)).thenReturn(newRefreshToken); // 새 리프레시 토큰 생성

        // When
        Map<String, Object> result = userService.refresh(cookie);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).containsKey("headers");
        HttpHeaders headers = (HttpHeaders) result.get("headers");
        assertThat(headers.get("Authorization")).containsExactly("Bearer " + newAccessToken);
        assertThat(headers.get("Set-Cookie")).isNotNull();
        assertThat(user.getRefreshToken()).isEqualTo(newRefreshToken); // User 엔티티의 리프레시 토큰 업데이트 확인

        // verify: Mock 객체의 메서드가 예상대로 호출되었는지 검증
        verify(jwtTokenProvider, times(1)).validateToken(refreshToken);
        verify(jwtTokenProvider, times(1)).getUserId(refreshToken);
        verify(userRepository, times(1)).findById(userId);
        verify(jwtTokenProvider, times(1)).generateAccessToken(String.valueOf(userId), nickname, oauthId);
        verify(jwtTokenProvider, times(1)).generateRefreshToken(String.valueOf(userId), nickname, oauthId);
    }

    @Test
    @DisplayName("refresh: Cookie가 null이면 TokenNotFoundException을 던진다.")
    void refresh_CookieIsNull_ThrowsTokenNotFoundException() {
        // Given
        Cookie cookie = null;

        // When & Then
        assertThatThrownBy(() -> userService.refresh(cookie))
                .isInstanceOf(TokenNotFoundException.class);

        // verify: Mock 객체의 메서드가 호출되지 않았는지 검증
        verify(jwtTokenProvider, never()).validateToken(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("refresh: refreshToken이 null이면 TokenInvalidException을 던진다.")
    void refresh_RefreshTokenIsNull_ThrowsTokenInvalidException() {
        // Given
        Cookie cookie = new Cookie("refreshToken", null);

        // When & Then
        assertThatThrownBy(() -> userService.refresh(cookie))
                .isInstanceOf(TokenInvalidException.class);

        verify(jwtTokenProvider, never()).validateToken(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("refresh: refreshToken이 빈 문자열이면 TokenInvalidException을 던진다.")
    void refresh_RefreshTokenIsEmpty_ThrowsTokenInvalidException() {
        // Given
        Cookie cookie = new Cookie("refreshToken", "");

        // When & Then
        assertThatThrownBy(() -> userService.refresh(cookie))
                .isInstanceOf(TokenInvalidException.class);

        verify(jwtTokenProvider, never()).validateToken(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("refresh: JwtTokenProvider.validateToken 실패 시 UnknownTokenException을 던진다.")
    void refresh_ValidateTokenFails_ThrowsUnknownTokenException() {
        // Given
        String refreshToken = "invalidRefreshToken";
        Cookie cookie = new Cookie("refreshToken", refreshToken);

        // Mocking: validateToken이 RuntimeException을 던지도록 설정
        when(jwtTokenProvider.validateToken(refreshToken)).thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        assertThatThrownBy(() -> userService.refresh(cookie))
                .isInstanceOf(UnknownTokenException.class);

        verify(jwtTokenProvider, times(1)).validateToken(refreshToken);
        verify(jwtTokenProvider, never()).getUserId(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("refresh: 저장된 리프레시 토큰과 요청의 리프레시 토큰이 일치하지 않으면 TokenMismatchException을 던진다.")
    void refresh_RefreshTokenMismatch_ThrowsTokenMismatchException() {
        // Given
        String refreshToken = "requestRefreshToken";
        String savedRefreshToken = "differentRefreshToken";
        String userId = "1";
        String nickname = "testUser";
        String oauthId = "testOauthId";

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        User user = User.createForTest(oauthId, "kakao", nickname, null); // 테스트용 User 생성
        user.updateRefreshToken(savedRefreshToken); // User에 다른 리프레시 토큰 설정

        // Mocking
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(refreshToken)).thenReturn(userId);
        when(userRepository.findById(Long.parseLong(userId))).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> userService.refresh(cookie))
                .isInstanceOf(TokenMismatchException.class);

        verify(jwtTokenProvider, times(1)).validateToken(refreshToken);
        verify(jwtTokenProvider, times(1)).getUserId(refreshToken);
        verify(userRepository, times(1)).findById(Long.parseLong(userId));
        verify(jwtTokenProvider, never()).generateAccessToken(any(), any(), any());
        verify(userRepository, never()).save(any(User.class));
    }
}