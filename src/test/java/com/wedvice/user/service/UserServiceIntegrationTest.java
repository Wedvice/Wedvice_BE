package com.wedvice.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.security.login.RedirectEnum;
import com.wedvice.security.login.RedirectResponseDto;
import com.wedvice.user.entity.User;
import com.wedvice.user.entity.User.Role;
import com.wedvice.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserService 통합 테스트")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

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
        assertThat(resultUser.getProfileImageUrl()).isEqualTo(
            initialProfileImageUrl); // 프로필 이미지가 업데이트되지 않아야 함
        assertThat(finalUserCount).isEqualTo(initialUserCount); // 사용자 수가 변하지 않아야 함
    }

    // * refresh (UPDATE): 기존 사용자 정보가 수정되는 흐름을 테스트. (추가하면 좋음)

    @Nested
    @DisplayName("getRedirectStatus 메서드 테스트")
    class GetRedirectStatus {

        @Autowired
        private CoupleRepository coupleRepository;

        @Test
        @DisplayName("User가 매칭되지 않은 경우 JUST_USER 반환")
        void unmatchedUserReturnsJustUser() {
            // given
            User user = User.create("oauth-id", "kakao");
            userRepository.save(user);

            // when
            System.out.println(em.contains(user));
            RedirectResponseDto result = userService.getRedirectStatus(user.getId());

            // then
            assertThat(result.getRedirectCode()).isEqualTo(RedirectEnum.JUST_USER.getNumber());
        }

        @Nested
        @DisplayName("커플 쌍이 필요한 테스트")
        class CoupleCompleted {

            User user;
            User partner;

            @BeforeEach
            void setup() {
                // given
                user = User.create("oauth-id", "kakao");
                partner = User.create("oauth-id2", "kakao");
                userRepository.save(user);
                userRepository.save(partner);

                Couple couple = Couple.create();
                coupleRepository.save(couple);
                user.matchCouple(couple); // 가상 커플 매칭
                partner.matchCouple(couple);
            }

            @Test
            @DisplayName("User가 정보 입력을 완료하지 않은 경우 Not_COMPLETED 반환")
            void userNotCompletedReturnsNotCompleted() {
                // when
                RedirectResponseDto result = userService.getRedirectStatus(user.getId());

                // then
                assertThat(result.getRedirectCode()).isEqualTo(
                    RedirectEnum.NOT_COMPLETED.getNumber());
            }

            @Test
            @DisplayName("User만 정보 입력 완료한 경우 ONLY_COMPLETED 반환")
            void userOnlyCompletedReturnsOnlyCompleted() {
                // given
                user.updateNickname("신랑");
                user.updateRole(Role.GROOM);
                userRepository.save(user);
                em.flush();

                // when
                RedirectResponseDto result = userService.getRedirectStatus(user.getId());

                // then
                assertThat(result.getRedirectCode()).isEqualTo(
                    RedirectEnum.ONLY_COMPLETED.getNumber());
            }

            @Test
            @DisplayName("전부 입력한 경우 PAIR_COMPLETED 반환")
            void userPairCompletedReturnsPairCompleted() {
                // given
                user.updateNickname("신랑");
                user.updateRole(Role.GROOM);
                userRepository.save(user);
                partner.updateNickname("신부");
                partner.updateRole(Role.BRIDE);
                userRepository.save(partner);
                em.flush();

                // when
                RedirectResponseDto result = userService.getRedirectStatus(user.getId());

                // then
                assertThat(result.getRedirectCode()).isEqualTo(
                    RedirectEnum.PAIR_COMPLETED.getNumber());
            }
        }
    }
}
