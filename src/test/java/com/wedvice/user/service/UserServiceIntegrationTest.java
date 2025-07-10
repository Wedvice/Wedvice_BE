package com.wedvice.user.service;

import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserService 통합 테스트")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("saveOrGetUser: 새로운 사용자는 DB에 저장된다.")
    void saveOrGetUser_newUser_isSavedInDB() {
        // Given
        String oauthId = "new-oauth-id-integration";
        String provider = "kakao";
        String profileImageUrl = "http://new.profile.image/url.jpg";

        // When
        User resultUser = userService.saveOrGetUser(oauthId, provider, profileImageUrl);

        // Then
        User foundUser = userRepository.findByOauthId(oauthId).orElse(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(resultUser.getId());
        assertThat(foundUser.getProvider()).isEqualTo(provider);
        assertThat(foundUser.getProfileImageUrl()).isEqualTo(profileImageUrl);
    }

    @Test
    @DisplayName("saveOrGetUser: 기존 사용자는 DB에 다시 저장되지 않고, 기존 정보가 반환된다.")
    void saveOrGetUser_existingUser_isNotSavedAgain() {
        // Given
        String oauthId = "existing-oauth-id-integration";
        String provider = "google";
        String initialProfileImageUrl = "http://initial.profile.image/url.jpg";

        // 미리 사용자를 저장
        User existingUser = User.create(oauthId, provider);
        existingUser.updateProfileImage(initialProfileImageUrl);
        userRepository.saveAndFlush(existingUser);

        long initialUserCount = userRepository.count();

        // When
        String newProfileImageUrl = "http://new.profile.image/url.jpg";
        User resultUser = userService.saveOrGetUser(oauthId, provider, newProfileImageUrl);

        // Then
        long finalUserCount = userRepository.count();

        assertThat(resultUser.getId()).isEqualTo(existingUser.getId());
        assertThat(resultUser.getProfileImageUrl()).isEqualTo(initialProfileImageUrl); // 프로필 이미지가 업데이트되지 않아야 함
        assertThat(finalUserCount).isEqualTo(initialUserCount); // 사용자 수가 변하지 않아야 함
    }


        // * refresh (UPDATE): 기존 사용자 정보가 수정되는 흐름을 테스트. (추가하면 좋음)
}
