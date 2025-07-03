package com.wedvice.user.entity;

import com.wedvice.couple.entity.Couple;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User 엔티티 단위 테스트")
class UserTest {

    @Nested
    @DisplayName("create 메서드")
    class CreateMethod {

        @Test
        @DisplayName("필수 필드로 User 객체가 올바르게 생성되어야 한다.")
        void shouldCreateUserWithRequiredFields() {
            // Given
            String oauthId = "testOauthId";
            String provider = "testProvider";

            // When
            User user = User.create(oauthId, provider);

            // Then
            assertThat(user).isNotNull();
            assertThat(user).isNotNull();
            assertEquals(oauthId, user.getOauthId());
            assertEquals(provider, user.getProvider());
            assertEquals(User.Role.USER, user.getRole());
            assertNotEquals(User.Role.GROOM,user.getRole());
        }
    }

    @Nested
    @DisplayName("updateNickname 메서드")
    class UpdateNicknameMethod {

        @Test
        @DisplayName("닉네임이 올바르게 변경되어야 한다.")
        void shouldUpdateNickname() {
            // Given
            User user = User.create("oauthId1", "provider1");
            String newNickname = "새로운닉네임";

            // When
            user.updateNickname(newNickname);

            // Then
            assertEquals(newNickname, user.getNickname());
        }
    }

    @Nested
    @DisplayName("updateProfileImage 메서드")
    class UpdateProfileImageMethod {

        @Test
        @DisplayName("프로필 이미지가 올바르게 변경되어야 한다.")
        void shouldUpdateProfileImage() {
            // Given
            User user = User.create("oauthId_profile", "provider_profile");
            String newProfileImageUrl = "http://new.profile.image/url.jpg";

            // When
            user.updateProfileImage(newProfileImageUrl);

            // Then
            assertEquals(newProfileImageUrl, user.getProfileImageUrl());
        }
    }

    @Nested
    @DisplayName("updateMemo 메서드")
    class UpdateMemoMethod {

        @Test
        @DisplayName("메모가 올바르게 변경되어야 한다.")
        void shouldUpdateMemo() {
            // Given
            User user = User.create("oauthId3", "provider3");
            String newMemo = "새로운 메모입니다.";

            // When
            user.updateMemo(newMemo);

            // Then
            assertEquals(newMemo, user.getMemo());
        }
    }

    @Nested
    @DisplayName("updateRefreshToken 메서드")
    class UpdateRefreshTokenMethod {

        @Test
        @DisplayName("리프레시 토큰이 올바르게 변경되어야 한다.")
        void shouldUpdateRefreshToken() {
            // Given
            User user = User.create("oauthId4", "provider4");
            String newRefreshToken = "newRefreshTokenString";

            // When
            user.updateRefreshToken(newRefreshToken);

            // Then
            assertEquals(newRefreshToken, user.getRefreshToken());
        }
    }

    @Nested
    @DisplayName("updateRole 메서드")
    class UpdateRoleMethod {

        @Test
        @DisplayName("역할이 올바르게 변경되어야 한다.")
        void shouldUpdateRole() {
            // Given
            User user = User.create("oauthId2", "provider2");
            User.Role newRole = User.Role.BRIDE;

            // When
            user.updateRole(newRole);

            // Then
            assertEquals(newRole, user.getRole());
        }
    }





    @Nested
    @DisplayName("matchCouple 메서드")
    class MatchCoupleMethod {

        @Test
        @DisplayName("User와 Couple 간의 연관관계가 올바르게 설정되어야 한다.")
        void shouldMatchCouple() {
            // Given
            User user = User.create("oauthId5", "provider5");
            User partnerUser = User.create("partner","provider6");

            Couple couple = Couple.create();

            // When
            user.matchCouple(couple);
            partnerUser.matchCouple(couple);

            // Then
            assertNotNull(user.getCouple());
            assertNotNull(partnerUser.getCouple());
            assertEquals(partnerUser.getCouple(),user.getCouple());
            assertEquals(2, user.getCouple().getUsers().size());
            assertEquals(user.getCouple().getUsers().get(0),user);

            assertEquals(couple, user.getCouple());
            assertTrue(couple.getUsers().contains(user));
            assertTrue(couple.getUsers().contains(partnerUser));
            assertEquals(couple.getUsers().size(),2);

        }
    }
}