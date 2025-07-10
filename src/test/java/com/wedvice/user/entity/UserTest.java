package com.wedvice.user.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wedvice.couple.entity.Couple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            assertThat(user.getNickname()).isNull();
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
        @DisplayName("닉네임이 올바르게 변경되어야 한다. (1자 또는 2자)")
        void shouldUpdateNickname() {
            // Given
            User user1 = User.create("oauthId1", "provider1");
            String nickname1 = "A"; // 1자 닉네임

            User user2 = User.create("oauthId2", "provider2");
            String nickname2 = "AB"; // 2자 닉네임

            // When
            user1.updateNickname(nickname1);
            user2.updateNickname(nickname2);

            // Then
            assertEquals(nickname1, user1.getNickname());
            assertEquals(nickname2, user2.getNickname());
        }

        @Test
        @DisplayName("닉네임이 2자를 초과하면 IllegalArgumentException이 발생해야 한다.")
        void shouldThrowExceptionWhenNicknameIsMoreThanTwoCharacters() {
            // Given
            User user = User.create("oauthId_long", "provider_long");
            String longNickname = "ABC"; // 3자 닉네임

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> user.updateNickname(longNickname));
        }

        @Test
        @DisplayName("닉네임이 null이면 IllegalArgumentException이 발생해야 한다.")
        void shouldThrowExceptionWhenNicknameIsNull() {
            // Given
            User user = User.create("oauthId_null", "provider_null");
            String nullNickname = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> user.updateNickname(nullNickname));
        }

        @Test
        @DisplayName("닉네임이 공백이면 IllegalArgumentException이 발생해야 한다.")
        void shouldThrowExceptionWhenNicknameIsBlank() {
            // Given
            User user = User.create("oauthId_blank", "provider_blank");
            String blankNickname = " "; // 공백 닉네임

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> user.updateNickname(blankNickname));
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

        @Test
        @DisplayName("메모가 18자를 초과하면 IllegalArgumentException이 발생해야 한다.")
        void shouldThrowExceptionWhenMemoIsMoreThanEighteenCharacters() {
            // Given
            User user = User.create("oauthId_long_memo", "provider_long_memo");
            String longMemo = "이것은18자를초과하는매우긴메모입니다."; // 19자

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> user.updateMemo(longMemo));
        }

        @Test
        @DisplayName("메모가 null이면 null로 설정되어야 한다.")
        void shouldSetMemoToNullWhenNullIsProvided() {
            // Given
            User user = User.create("oauthId_null_memo", "provider_null_memo");
            user.updateMemo("기존 메모"); // 기존 메모 설정

            // When
            user.updateMemo(null);

            // Then
            assertNull(user.getMemo());
        }

        @Test
        @DisplayName("메모가 빈 문자열이면 빈 문자열로 설정되어야 한다.")
        void shouldSetMemoToEmptyWhenEmptyStringIsProvided() {
            // Given
            User user = User.create("oauthId_empty_memo", "provider_empty_memo");
            user.updateMemo("기존 메모"); // 기존 메모 설정

            // When
            user.updateMemo("");

            // Then
            assertEquals("", user.getMemo());
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
    @DisplayName("updateEmail 메서드")
    class UpdateEmailMethod {

        @Test
        @DisplayName("이메일이 올바르게 변경되어야 한다.")
        void shouldUpdateEmail() {
            // Given
            User user = User.create("oauthId_email", "provider_email");
            String newEmail = "new.email@example.com";

            // When
            user.updateEmail(newEmail);

            // Then
            assertEquals(newEmail, user.getEmail());
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