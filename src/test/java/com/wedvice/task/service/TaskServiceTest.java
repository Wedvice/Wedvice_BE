package com.wedvice.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.exception.InvalidUserAccessException;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.coupletask.service.CoupleTaskService;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.task.dto.TaskResponseDTO;
import com.wedvice.user.entity.User;
import com.wedvice.user.service.UserService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {


    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_COUPLE_ID = 1L;


    @Mock
    private UserService userService;
    @Mock
    private CoupleTaskService coupleTaskService;
    @InjectMocks
    private TaskService taskService;
    private CustomUserDetails customUserDetails;
    private User testUser;
    private Couple testCouple;

    @BeforeEach
    void setUp() {
        // Couple 객체 Mocking (ID는 상수로 대체)
        testCouple = mock(Couple.class);

        // User 객체 Mocking (ID는 상수로 대체)
        testUser = mock(User.class);

        // CustomUserDetails 생성자 수정
        customUserDetails = new CustomUserDetails(TEST_USER_ID, "test@example.com",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        lenient().when(userService.getCoupleIdForUser(TEST_USER_ID))
            .thenReturn(TEST_COUPLE_ID);
    }

    @Test
    @DisplayName("다중 Task를 성공적으로 소프트 삭제해야 한다")
    void shouldSoftDeleteMultipleTasksSuccessfully() {
        // Given
        List<Long> taskIds = Arrays.asList(10L, 11L);

        // When
        taskService.deleteTasks(taskIds, customUserDetails);

        // Then
        verify(userService, times(1)).getCoupleIdForUser(TEST_USER_ID);
        verify(coupleTaskService, times(1)).softDeleteCoupleTasks(taskIds, TEST_COUPLE_ID);
    }

    @Test
    @DisplayName("Task ID가 존재하지 않으면 예외를 발생시켜야 한다")
    void shouldThrowExceptionWhenTaskNotFound() {
        // Given
        List<Long> taskIds = Arrays.asList(10L, 11L);

        // When coupleTaskService throws an exception for softDeleteCoupleTasks
        doThrow(new RuntimeException("Some tasks not found or permission denied."))
            .when(coupleTaskService).softDeleteCoupleTasks(taskIds, TEST_COUPLE_ID);

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTasks(taskIds, customUserDetails))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Some tasks not found or permission denied.");

        verify(userService, times(1)).getCoupleIdForUser(TEST_USER_ID);
        verify(coupleTaskService, times(1)).softDeleteCoupleTasks(taskIds, TEST_COUPLE_ID);
    }


    @Test
    @DisplayName("findAllCoupleTaskAndSubTask: 올바른 TaskResponseDTO 리스트를 반환해야 한다")
    void shouldReturnCorrectTaskResponseDTOs() {
        // Given
        com.wedvice.task.entity.Task mockTask1 = mock(com.wedvice.task.entity.Task.class);
        when(mockTask1.getId()).thenReturn(1L);
        when(mockTask1.getTitle()).thenReturn("Task 1");

        com.wedvice.task.entity.Task mockTask2 = mock(com.wedvice.task.entity.Task.class);
        when(mockTask2.getId()).thenReturn(2L);
        when(mockTask2.getTitle()).thenReturn("Task 2");

        CoupleTask coupleTask1 = mock(CoupleTask.class);
        when(coupleTask1.getTask()).thenReturn(mockTask1);
        // SubTask mocking for coupleTask1
        List<SubTask> subTasks1 = Arrays.asList(mock(SubTask.class), mock(SubTask.class),
            mock(SubTask.class));
        when(subTasks1.get(0).getCompleted()).thenReturn(true);
        when(subTasks1.get(1).getCompleted()).thenReturn(false);
        when(subTasks1.get(2).getCompleted()).thenReturn(true);
        when(coupleTask1.getSubTasks()).thenReturn(subTasks1);

        CoupleTask coupleTask2 = mock(CoupleTask.class);
        when(coupleTask2.getTask()).thenReturn(mockTask2);
        // SubTask mocking for coupleTask2
        List<SubTask> subTasks2 = Collections.singletonList(mock(SubTask.class));
        when(subTasks2.get(0).getCompleted()).thenReturn(false);
        when(coupleTask2.getSubTasks()).thenReturn(subTasks2);

        List<CoupleTask> mockCoupleTasks = Arrays.asList(coupleTask1, coupleTask2);
        when(coupleTaskService.findByCoupleIdWithTask(TEST_COUPLE_ID)).thenReturn(
            mockCoupleTasks);

        // When
        List<TaskResponseDTO> result = taskService.findAllCoupleTaskAndSubTask(customUserDetails);

        // Then
        assertThat(result).hasSize(2);

        assertThat(result.get(0)).satisfies(dto -> {
            assertThat(dto.getTaskId()).isEqualTo(1L);
            assertThat(dto.getTaskTitle()).isEqualTo("Task 1");
            assertThat(dto.getTotalCount()).isEqualTo(3L);
            assertThat(dto.getCompletedCount()).isEqualTo(2);
        });

        assertThat(result.get(1)).satisfies(dto -> {
            assertThat(dto.getTaskId()).isEqualTo(2L);
            assertThat(dto.getTaskTitle()).isEqualTo("Task 2");
            assertThat(dto.getTotalCount()).isEqualTo(1L);
            assertThat(dto.getCompletedCount()).isEqualTo(0);
        });

        verify(userService, times(1)).getCoupleIdForUser(TEST_USER_ID);
        verify(coupleTaskService, times(1)).findByCoupleIdWithTask(TEST_COUPLE_ID);
    }

    @Test
    @DisplayName("findAllCoupleTaskAndSubTask: CoupleTask가 없을 때 빈 리스트를 반환해야 한다")
    void shouldReturnEmptyListWhenNoCoupleTasks() {
        // Given
        when(coupleTaskService.findByCoupleIdWithTask(TEST_COUPLE_ID)).thenReturn(
            Collections.emptyList());

        // When
        List<TaskResponseDTO> result = taskService.findAllCoupleTaskAndSubTask(customUserDetails);

        // Then
        assertThat(result).isNotNull().isEmpty();

        verify(userService, times(1)).getCoupleIdForUser(TEST_USER_ID);
        verify(coupleTaskService, times(1)).findByCoupleIdWithTask(TEST_COUPLE_ID);
    }

    @Test
    @DisplayName("findAllCoupleTaskAndSubTask: 사용자를 찾을 수 없을 때 예외를 발생시켜야 한다")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userService.getCoupleIdForUser(TEST_USER_ID)).thenThrow(
            new InvalidUserAccessException()); // UserService에서 던지는 예외

        // When & Then
        assertThatExceptionOfType(InvalidUserAccessException.class).isThrownBy(() ->
            taskService.findAllCoupleTaskAndSubTask(customUserDetails)
        );

        verify(userService, times(1)).getCoupleIdForUser(TEST_USER_ID);
        verify(coupleTaskService, never()).findByCoupleIdWithTask(any());
    }
}

