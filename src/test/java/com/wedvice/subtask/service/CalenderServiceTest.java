package com.wedvice.subtask.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.wedvice.couple.entity.Couple;
import com.wedvice.subtask.SubTaskTestFixture;
import com.wedvice.subtask.dto.CalenderSummaryResponseDto;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.subtask.repository.SubTaskRepository;
import com.wedvice.user.common.UserTestFixture;
import com.wedvice.user.entity.User;
import com.wedvice.user.entity.User.Role;
import com.wedvice.user.entity.UserConfig;
import com.wedvice.user.entity.UserConfig.Color;
import com.wedvice.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class CalenderServiceTest {

    @InjectMocks
    private CalenderService calenderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubTaskRepository subTaskRepository;

    @Test
    @DisplayName("makeSummary는 유저와 SubTask 정보를 바탕으로 요약 DTO를 반환한다")
    void makeSummaryReturnsExpectedDto() {
        // given
        Long userId = 1L;
        int year = 2024;
        int month = 12;

        User user = UserTestFixture.createWithRoleAndNickNameInjectId(Role.GROOM, "신부", userId);
        UserConfig uc = UserConfig.builder()
            .myColor(Color.RED)
            .yourColor(Color.BLUE)
            .ourColor(Color.GREEN)
            .build();
        user.assignUserConfig(uc);
        Couple couple = Couple.create();
        user.matchCouple(couple);
        ReflectionTestUtils.setField(couple, "id", 1L);

        when(userRepository.findByUserWithCoupleAndPartner(userId)).thenReturn(Optional.of(user));

        SubTask task1 = SubTaskTestFixture.createSubTask(1L, "예약하기", LocalDate.of(2024, 12, 10));
        SubTask task2 = SubTaskTestFixture.createSubTask(2L, "드레스 고르기", LocalDate.of(2024, 12, 20));
        when(subTaskRepository.getSubTasksByDate(anyLong(), eq(year), eq(month)))
            .thenReturn(List.of(task1, task2));

        // when
        CalenderSummaryResponseDto result = calenderService.makeSummary(userId, year, month);

        // then
        assertThat(result.getGroomColor()).isEqualTo(Color.RED);
        assertThat(result.getBrideColor()).isEqualTo(Color.BLUE);
        assertThat(result.getTogetherColor()).isEqualTo(Color.GREEN);
        assertThat(result.getSubTaskSummaryDto()).hasSize(2);
        assertThat(result.getSubTaskSummaryDto().get(0).getContent()).isEqualTo("예약하기");
    }
}