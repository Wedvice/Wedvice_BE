package com.wedvice.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.wedvice.couple.exception.InvalidUserAccessException;
import com.wedvice.security.login.RedirectEnum;
import com.wedvice.security.login.RedirectResponseDto;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트-JH")
public class UserServiceUnitTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Nested
    @DisplayName("getRedirectStatus 메서드 테스트")
    class GetRedirectStatus {

        @Test
        @DisplayName("유저가 커플이 아니라면 JUST_USER 반환")
        void returnJustUserIfNotMatched() {
            //given
            User user = mock(User.class);

            //when
            when(user.isMatched()).thenReturn(false);
            when(userRepository.findByUserWithCoupleAndPartner(anyLong())).thenReturn(
                Optional.of(user));
            RedirectResponseDto result = userService.getRedirectStatus(1L);

            //then
            assertEquals(RedirectEnum.JUST_USER.getNumber(), result.getRedirectCode());
        }

        @Test
        @DisplayName("유저 정보가 미완성이라면 NOT_COMPLETED 반환")
        void returnNotCompletedIfUserInfoIncomplete() {
            User user = mock(User.class);
            when(user.isMatched()).thenReturn(true);
            when(user.isInfoCompleted()).thenReturn(false);
            when(userRepository.findByUserWithCoupleAndPartner(anyLong())).thenReturn(
                Optional.of(user));

            RedirectResponseDto result = userService.getRedirectStatus(1L);
            assertEquals(RedirectEnum.NOT_COMPLETED.getNumber(), result.getRedirectCode());
        }

        @Test
        @DisplayName("파트너 정보가 미완성이라면 ONLY_COMPLETED 반환")
        void returnOnlyCompletedIfPartnerIncomplete() {
            User user = mock(User.class);
            when(user.isMatched()).thenReturn(true);
            when(user.isInfoCompleted()).thenReturn(true);
            when(user.isPartnerInfoCompleted()).thenReturn(false);
            when(userRepository.findByUserWithCoupleAndPartner(anyLong())).thenReturn(
                Optional.of(user));

            RedirectResponseDto result = userService.getRedirectStatus(1L);
            assertEquals(RedirectEnum.ONLY_COMPLETED.getNumber(), result.getRedirectCode());
        }

        @Test
        @DisplayName("모든 정보가 완성되었다면 PAIR_COMPLETED 반환")
        void returnPairCompletedIfAllComplete() {
            User user = mock(User.class);

            when(user.isMatched()).thenReturn(true);
            when(user.isInfoCompleted()).thenReturn(true);
            when(user.isPartnerInfoCompleted()).thenReturn(true);
            when(userRepository.findByUserWithCoupleAndPartner(anyLong())).thenReturn(
                Optional.of(user));

            RedirectResponseDto result = userService.getRedirectStatus(1L);
            assertEquals(RedirectEnum.PAIR_COMPLETED.getNumber(), result.getRedirectCode());
        }

        @Test
        @DisplayName("해당 유저가 존재하지 않으면 InvalidUserAccessException 발생")
        void throwExceptionIfUserNotFound() {
            when(userRepository.findByUserWithCoupleAndPartner(anyLong())).thenReturn(
                Optional.empty());

            assertThrows(InvalidUserAccessException.class,
                () -> userService.getRedirectStatus(999L));
        }
    }

    @Nested
    @DisplayName("updateMemo 메서드 테스트")
    @Disabled("SpringBootTest 환경에서 통합 테스트로 작성 예정")
    class UpdateMemo {

    }
}
