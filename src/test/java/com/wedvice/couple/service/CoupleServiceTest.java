package com.wedvice.couple.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.wedvice.couple.dto.CompleteMatchRequestDto;
import com.wedvice.couple.dto.CoupleHomeInfoResponseDto;
import com.wedvice.couple.dto.Gender;
import com.wedvice.couple.dto.UserDto;
import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.exception.AlreadyMatchedException;
import com.wedvice.couple.exception.InvalidUserAccessException;
import com.wedvice.couple.exception.NotMatchedYetException;
import com.wedvice.couple.exception.PartnerIncompleteException;
import com.wedvice.couple.exception.PartnerMustEnterMatchCode;
import com.wedvice.couple.exception.SameRoleException;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.couple.util.MatchCodeService;
import com.wedvice.task.service.TaskService;
import com.wedvice.user.entity.User;
import com.wedvice.user.entity.User.Role;
import com.wedvice.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CoupleServiceTest {

    @InjectMocks
    CoupleService coupleService;

    @Mock
    CoupleRepository coupleRepository;
    @Mock
    MatchCodeService matchCodeService;
    @Mock
    UserRepository userRepository;
    @Mock
    TaskService taskService;

    void setUserId(User user, Long userId) {
        ReflectionTestUtils.setField(user, "id", userId);
    }

    @Nested
    @DisplayName("completeMatch 메서드 테스트")
    class CompleteMatch {

    }

    @Nested
    @DisplayName("getCoupleInfo 메서드 테스트")
    class GetCoupleInfo {

        private User createTestUser(Long id, String provider, String oauthId, String nickname,
            String imageUrl,
            String memo, Role role) {
            User user = User.create(oauthId, provider);
            user.updateNickname(nickname);
            user.updateProfileImage(imageUrl);
            user.updateMemo(memo);
            user.updateRole(role);
            setUserId(user, id);
            return user;
        }

        private void assertUserDto(UserDto dto, String expectedImage, String expectedMemo,
            boolean isPartner, String label) {
            assertAll(label,
                () -> assertThat(dto.getImageUrl()).isEqualTo(expectedImage),
                () -> assertThat(dto.getMemo()).isEqualTo(expectedMemo),
                () -> assertThat(dto.isPartner()).isEqualTo(isPartner)
            );
        }


        @Test
        @DisplayName("유저와 파트너가 정상 설정된 경우, 커플 정보를 반환한다")
        void success() {
            // given
            Long userId = 1L;
            Long partnerId = 2L;
            LocalDate weddingDate = LocalDate.of(2025, 10, 21);

            Couple couple = Couple.create();
            couple.updateWeddingDate(weddingDate);

// 유저 정보
            String userOauthId = "userOauthId";
            String userProvider = "kakao";
            String userNickname = "웨디";
            String userImage = "프로필1";
            String userMemo = "바보";
            Role userRole = Role.GROOM;

// 파트너 정보
            String partnerOauthId = "partnerOauthId";
            String partnerProvider = "naver";
            String partnerNickname = "파트너";
            String partnerImage = "프로필2";
            String partnerMemo = "멍청이";
            Role partnerRole = Role.BRIDE;

            User user = createTestUser(userId, userOauthId, userProvider, userNickname, userImage,
                userMemo, userRole);
            User partner = createTestUser(partnerId, partnerOauthId, partnerProvider,
                partnerNickname, partnerImage, partnerMemo, partnerRole);

            // 연관관계 연결
            user.matchCouple(couple);
            partner.matchCouple(couple);

            given(userRepository.findByUserWithCoupleAndPartner(userId))
                .willReturn(Optional.of(user));

            // when
            CoupleHomeInfoResponseDto result = coupleService.getCoupleInfo(userId);

            // then
            assertUserDto(result.getGroomDto(), userImage, userMemo, false, "Groom DTO 검증");
            assertUserDto(result.getBrideDto(), partnerImage, partnerMemo, true, "Bride DTO 검증");

            assertThat(result.getWeddingDate()).isEqualTo(weddingDate);
        }

        @DisplayName("조회된 유저가 없으면 예외를 던진다")
        @Test
        void throwIfUserNotFound() {
            Long userId = 1L;
            given(userRepository.findByUserWithCoupleAndPartner(userId))
                .willReturn(Optional.empty());

            assertThrows(InvalidUserAccessException.class, () -> {
                coupleService.getCoupleInfo(userId);
            });
        }

        @Test
        @DisplayName("커플이 매칭되지 않았으면 예외를 던진다")
        void throwIfUserNotMatched() {
            User user = User.create("user", "kakao");
            setUserId(user, 1L); // id 필수

            given(userRepository.findByUserWithCoupleAndPartner(1L))
                .willReturn(Optional.of(user));

            assertThrows(PartnerMustEnterMatchCode.class, () -> {
                coupleService.getCoupleInfo(1L);
            });
        }

        @Test
        @DisplayName("자신의 정보가 입력되지 않았으면 예외를 던진다")
        void throwIfUserInfoIncomplete() {
            Couple couple = Couple.create();
            User user = User.create("user", "kakao");
            setUserId(user, 1L);
            user.matchCouple(couple); // 커플 매칭은 되어 있음

            given(userRepository.findByUserWithCoupleAndPartner(1L))
                .willReturn(Optional.of(user));

            assertThrows(NotMatchedYetException.class, () -> {
                coupleService.getCoupleInfo(1L);
            });
        }

        @Test
        @DisplayName("파트너의 정보가 입력되지 않았으면 예외를 던진다")
        void throwIfPartnerInfoIncomplete() {
            Long userId = 1L;
            Long partnerId = 2L;
            Couple couple = Couple.create();

            User user = createTestUser(userId, "kakao", "user", "웨디", "이미지", "메모", User.Role.GROOM);
            User partner = User.create("partner", "naver"); // 닉네임/역할 없음
            setUserId(partner, partnerId);

            user.matchCouple(couple);
            partner.matchCouple(couple);

            given(userRepository.findByUserWithCoupleAndPartner(userId))
                .willReturn(Optional.of(user));

            assertThrows(PartnerIncompleteException.class, () -> {
                coupleService.getCoupleInfo(userId);
            });
        }
    }

    @Nested
    @DisplayName("matchCouple 메서드 테스트")
    class MatchCouple {

        Long userId;
        Long partnerId;
        String userNickname;
        String partnerNickname;
        String userOauthId;
        String partnerOauthId;
        String userProvider;
        String partnerProvider;
        Couple couple;
        User user;
        User partner;

        @BeforeEach
        void setup() {
            userId = 1L;
            partnerId = 2L;

            userNickname = "유저";
            partnerNickname = "파트너";

            userOauthId = "userOauthId";
            partnerOauthId = "partnerOauthId";

            userProvider = "kakao";
            partnerProvider = "naver";

            couple = Couple.create();

            user = User.create(userOauthId, userProvider);
            partner = User.create(partnerOauthId, partnerProvider);

            setUserId(user, userId);
            setUserId(partner, partnerId);

            user.matchCouple(couple);
            partner.matchCouple(couple);
        }

        @Test
        @DisplayName("상대방이 입력이 완료된 상황일 때 정상적으로 매칭을 완료한다.")
        void successWhenPartnerIsCompleted() {
            // given
            Gender userGender = Gender.BRIDE;
            User.Role expectedRole = User.Role.BRIDE;
            User.Role partnerRole = User.Role.GROOM;

            partner.updateNickname(partnerNickname);
            partner.updateRole(partnerRole);

            given(userRepository.findByUserWithCoupleAndPartner(userId))
                .willReturn(Optional.of(user));

            CompleteMatchRequestDto requestDto = new CompleteMatchRequestDto(userNickname,
                userGender);

            // when
            coupleService.completeMatch(userId, requestDto);

            // then
            assertAll("유저 매칭 정보 검증",
                () -> assertThat(user.getNickname()).isEqualTo(userNickname),
                () -> assertThat(user.getRole()).isEqualTo(expectedRole)
            );
        }

        @DisplayName("상대방도 입력되지 않은 경우, 예외 없이 매칭을 완료한다.")
        @Test
        void successWhenBothUserRole() {
            // given
            Gender userGender = Gender.GROOM;
            User.Role expectedRole = User.Role.GROOM;

            // partner는 아무런 정보 입력되지 않은 상태 (Role.USER)
            given(userRepository.findByUserWithCoupleAndPartner(userId))
                .willReturn(Optional.of(user));

            CompleteMatchRequestDto requestDto = new CompleteMatchRequestDto(userNickname,
                userGender);

            // when
            coupleService.completeMatch(userId, requestDto);

            // then
            assertAll("내 정보가 정상적으로 입력되었는지 검증",
                () -> assertThat(user.getNickname()).isEqualTo(userNickname),
                () -> assertThat(user.getRole()).isEqualTo(expectedRole)
            );
        }

        @Test
        @DisplayName("상대방과 같은 역할 BRIDE을 선택하면 SameRoleException을 던진다.")
        void throwIfSameBRIDERoleAsPartner() {
            //given
            Gender partnerGender = Gender.BRIDE;
            Gender userGender = Gender.BRIDE;

            CompleteMatchRequestDto partnerDto = new CompleteMatchRequestDto(partnerNickname,
                partnerGender);
            given(userRepository.findByUserWithCoupleAndPartner(partnerId)).willReturn(
                Optional.of(partner));
            given(userRepository.findByUserWithCoupleAndPartner(userId)).willReturn(
                Optional.of(user));
            coupleService.completeMatch(partnerId, partnerDto);

            // when
            CompleteMatchRequestDto userDto = new CompleteMatchRequestDto(userNickname, userGender);
            assertThrows(SameRoleException.class,
                () -> coupleService.completeMatch(userId, userDto));
        }

        @Test
        @DisplayName("상대방과 같은 역할 GROOM을 선택하면 SameRoleException을 던진다.")
        void throwIfSameRoleAsPartner() {
            //given
            Gender partnerGender = Gender.GROOM;
            Gender userGender = Gender.GROOM;

            CompleteMatchRequestDto partnerDto = new CompleteMatchRequestDto(partnerNickname,
                partnerGender);
            given(userRepository.findByUserWithCoupleAndPartner(partnerId)).willReturn(
                Optional.of(partner));
            given(userRepository.findByUserWithCoupleAndPartner(userId)).willReturn(
                Optional.of(user));
            coupleService.completeMatch(partnerId, partnerDto);

            // when
            CompleteMatchRequestDto userDto = new CompleteMatchRequestDto(userNickname, userGender);
            assertThrows(SameRoleException.class,
                () -> coupleService.completeMatch(userId, userDto));
        }

        @DisplayName("이미 역할이 입력된 경우 AlreadyMatchedException을 던진다.")
        @Test
        void throwIfAlreadyMatched() {
            // given
            user.updateNickname(userNickname);
            user.updateRole(Role.BRIDE); // 이미 역할이 설정됨

            given(userRepository.findByUserWithCoupleAndPartner(userId))
                .willReturn(Optional.of(user));

            CompleteMatchRequestDto requestDto = new CompleteMatchRequestDto(userNickname,
                Gender.BRIDE);

            // when & then
            assertThrows(
                AlreadyMatchedException.class,
                () -> coupleService.completeMatch(userId, requestDto));
        }
    }
}