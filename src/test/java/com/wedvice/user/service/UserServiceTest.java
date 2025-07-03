package com.wedvice.user.service;

import com.wedvice.user.dto.MemoRequestDto;
import com.wedvice.user.dto.UserDto;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("새로운 사용자 저장 또는 기존 사용자 조회 테스트 - 새로운 사용자 저장")
    void saveOrGetUser_saveNewUser() {
        // Given
        String oauthId = "test_oauth_id_new";
        String provider = "kakao";
        String profileImageUrl = "http://example.com/new_profile.jpg";

        // When
        User user = userService.saveOrGetUser(oauthId, provider, profileImageUrl);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getOauthId()).isEqualTo(oauthId);
        assertThat(user.getProvider()).isEqualTo(provider);
        assertThat(user.getProfileImageUrl()).isEqualTo(profileImageUrl);
        assertThat(userRepository.findByOauthId(oauthId)).isPresent();
    }

}
