package com.wedvice.user.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.exception.NotMatchedYetException;
import com.wedvice.couple.exception.PartnerNotFoundException;
import com.wedvice.user.entity.User.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

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
            assertNotEquals(User.Role.GROOM, user.getRole());
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
            User partnerUser = User.create("partner", "provider6");

            Couple couple = Couple.create();

            // When
            user.matchCouple(couple);
            partnerUser.matchCouple(couple);

            // Then
            assertNotNull(user.getCouple());
            assertNotNull(partnerUser.getCouple());
            assertEquals(partnerUser.getCouple(), user.getCouple());
            assertEquals(2, user.getCouple().getUsers().size());
            assertEquals(user.getCouple().getUsers().get(0), user);

            assertEquals(couple, user.getCouple());
            assertTrue(couple.getUsers().contains(user));
            assertTrue(couple.getUsers().contains(partnerUser));
            assertEquals(couple.getUsers().size(), 2);

        }
    }

    @Nested
    @DisplayName("getPartnerOrThrow 메서드")
    class GetPartnerOrThrow {

        @Test
        @DisplayName("파트너가 존재한다면 파트너를 반환한다.")
        void returnPartner() {
            //given
            User user = User.create("userId", "kakao");
            User partner = User.create("partnerId", "naver");
            Couple couple = Couple.create();

            ReflectionTestUtils.setField(user, "id", 1L);
            ReflectionTestUtils.setField(partner, "id", 2L);

            user.matchCouple(couple);
            partner.matchCouple(couple);

            //when & then
            assertThat(user.getPartnerOrThrow()).isEqualTo(partner);
        }

        @Test
        @DisplayName("파트너가 존재하지 않는다면 에러를 반환한다.")
        void throwNotMatchedYetException() {
            //given
            User user = User.create("userId", "kakao");
            Couple couple = Couple.create();

            user.matchCouple(couple);
            ReflectionTestUtils.setField(user, "id", 1L);

            //when & then
            assertThrows(PartnerNotFoundException.class, user::getPartnerOrThrow);
        }

        @Test
        @DisplayName("커플이 존재하지 않는다면 에러를 반환한다.")
        void throwNotCoupleException() {
            //given
            User user = User.create("userId", "kakao");

            //when & then
            assertThrows(NotMatchedYetException.class, user::getPartnerOrThrow);
        }
    }

    @Nested
    @DisplayName("isMatched 메서드")
    class IsMatched {

        @Test
        @DisplayName("커플이 존재하면 true를 반환한다.")
        void coupleExistReturnTrue() {

        }

        @Test
        @DisplayName("커플이 존재하지 않으면 false를 반환한다.")
        void coupleNotExistReturnTrue() {
        }
    }

    // [issue] updateRole에서 null명시적 변환이 가능한것 도메인에서 체크해야하지 않나?
    @Nested
    @DisplayName("isInfoCompleted 메서드")
    class IsInfoCompleted {

        @Test
        @DisplayName("닉네임과 역할이 모두 존재하면 true를 반환한다.")
        void returnTrueIfNicknameAndRoleExist() {
            User user = User.create("id", "kakao");
            user.updateNickname("닉네");
            user.updateRole(User.Role.GROOM); // Role.USER든 GROOM이든 중요하지 않음
            assertThat(user.isInfoCompleted()).isTrue();
        }

        @Test
        @DisplayName("닉네임이 없으면 false를 반환한다.")
        void returnFalseIfNicknameIsNull() {
            User user = User.create("id", "kakao");
            user.updateRole(User.Role.GROOM);
            assertThat(user.isInfoCompleted()).isFalse();
        }

        @Test
        @DisplayName("역할이 없으면 false를 반환한다.")
        void returnFalseIfRoleIsNull() {
            User user = User.create("id", "kakao");
            user.updateNickname("닉네");
            user.updateRole(null); // 명시적으로 role 제거
            assertThat(user.isInfoCompleted()).isFalse();
        }

        @Test
        @DisplayName("닉네임과 역할이 모두 없으면 false를 반환한다.")
        void returnFalseIfBothNull() {
            User user = User.create("id", "kakao");
            user.updateRole(null); // 명시적
            assertThat(user.isInfoCompleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("isPartnerInfoCompleted 메서드")
    class IsPartnerInfoCompleted {

        User user;
        User partner;
        Couple couple;

        @BeforeEach
        void setUp() {
            user = User.create("userId", "kakao");
            partner = User.create("partnerId", "naver");
            couple = Couple.create();

            user.matchCouple(couple);
            partner.matchCouple(couple);
        }

        @Test
        @DisplayName("닉네임과 역할이 온전하면 ture를 반환한다.")
        void returnTrueIfPartnerBothExist() {
            // given
            partner.updateRole(Role.BRIDE);
            partner.updateNickname("신부");

            //when then
            assertThat(partner.isInfoCompleted()).isTrue();
        }

        @Test
        @DisplayName("파트너 정보에 닉네임이 없으면 false를 반환한다.")
        void returnFalseIfPartnerNicknameNull() {
            // given
            partner.updateRole(Role.BRIDE);

            //when then
            assertThat(partner.isInfoCompleted()).isFalse();
        }


        @Test
        @DisplayName("파트너 정보에 역할 없으면 false를 반환한다.")
        void returnFalseIfPartnerRoleNull() {
            // given
            partner.updateRole(Role.BRIDE);
            partner.updateRole(null);

            //when then
            assertThat(partner.isInfoCompleted()).isFalse();
        }

        @Test
        @DisplayName("닉네임과 역할이 모두 없으면 false를 반환한다.")
        void returnFalseIfPartnerBothNull() {
            User user = User.create("id", "kakao");
            user.updateRole(null); // 명시적

            assertThat(user.isInfoCompleted()).isFalse();
        }
    }
}