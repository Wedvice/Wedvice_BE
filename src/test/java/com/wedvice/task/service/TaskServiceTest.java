package com.wedvice.task.service;

import com.wedvice.couple.entity.Couple;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.coupletask.repository.CoupleTaskRepository;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.task.dto.TaskResponseDTO;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CoupleTaskRepository coupleTaskRepository;

    @InjectMocks
    private TaskService taskService;

    private CustomUserDetails customUserDetails;
    private User testUser;
    private Couple testCouple;

    @BeforeEach
    void setUp() {
        // Couple 객체 Mocking
        testCouple = mock(Couple.class);
        lenient().when(testCouple.getId()).thenReturn(1L);

        // User 객체 Mocking
        testUser = mock(User.class);
        lenient().when(testUser.getId()).thenReturn(1L);
        lenient().when(testUser.getCouple()).thenReturn(testCouple);

        // CustomUserDetails 생성자 수정
        customUserDetails = new CustomUserDetails(testUser.getId(), "test@example.com", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        lenient().when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
    }

    @Test
    @DisplayName("다중 Task를 성공적으로 소프트 삭제해야 한다")
    void shouldSoftDeleteMultipleTasksSuccessfully() {
        // Given
        List<Long> taskIds = Arrays.asList(10L, 11L);

        CoupleTask coupleTask1 = mock(CoupleTask.class);
        CoupleTask coupleTask2 = mock(CoupleTask.class);

        when(coupleTaskRepository.findByTaskIdAndCoupleId(10L, testCouple.getId())).thenReturn(Optional.of(coupleTask1));
        when(coupleTaskRepository.findByTaskIdAndCoupleId(11L, testCouple.getId())).thenReturn(Optional.of(coupleTask2));

        // When
        taskService.deleteTasks(taskIds, customUserDetails);

        // Then
        verify(coupleTaskRepository, times(1)).findByTaskIdAndCoupleId(10L, testCouple.getId());
        verify(coupleTaskRepository, times(1)).findByTaskIdAndCoupleId(11L, testCouple.getId());
        verify(coupleTask1, times(1)).updateDeleteStatus();
        verify(coupleTask2, times(1)).updateDeleteStatus();
    }

    @Test
    @DisplayName("Task ID가 존재하지 않으면 예외를 발생시켜야 한다")
    void shouldThrowExceptionWhenTaskNotFound() {
        // Given
        List<Long> taskIds = Arrays.asList(10L, 11L);

        CoupleTask coupleTask1 = mock(CoupleTask.class);

        when(coupleTaskRepository.findByTaskIdAndCoupleId(10L, testCouple.getId())).thenReturn(Optional.of(coupleTask1));
        when(coupleTaskRepository.findByTaskIdAndCoupleId(11L, testCouple.getId())).thenReturn(Optional.empty()); // 11L은 찾을 수 없음

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTasks(taskIds, customUserDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found or permission denied");

        verify(coupleTaskRepository, times(1)).findByTaskIdAndCoupleId(10L, testCouple.getId());
        verify(coupleTaskRepository, times(1)).findByTaskIdAndCoupleId(11L, testCouple.getId());
        verify(coupleTask1, times(1)).updateDeleteStatus(); // 10L에 대한 updateDeleteStatus는 호출되어야 함
    }

    @Test
    @DisplayName("빈 Task ID 리스트가 주어져도 오류 없이 처리해야 한다")
    void shouldHandleEmptyTaskList() {
        // Given
        List<Long> taskIds = Collections.emptyList();

        // When
        taskService.deleteTasks(taskIds, customUserDetails);

        // Then
        verify(coupleTaskRepository, never()).findByTaskIdAndCoupleId(any(), any()); // 어떤 쿼리도 호출되지 않아야 함
    }

    @Test
    @DisplayName("findAllTaskAndSubTask: 올바른 TaskResponseDTO 리스트를 반환해야 한다")
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
        List<SubTask> subTasks1 = Arrays.asList(mock(SubTask.class), mock(SubTask.class), mock(SubTask.class));
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
        when(coupleTaskRepository.findByCoupleIdWithTask(testCouple.getId())).thenReturn(mockCoupleTasks);

        // When
        List<TaskResponseDTO> result = taskService.findAllTaskAndSubTask(customUserDetails);

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

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(coupleTaskRepository, times(1)).findByCoupleIdWithTask(testCouple.getId());
    }

    @Test
    @DisplayName("findAllTaskAndSubTask: CoupleTask가 없을 때 빈 리스트를 반환해야 한다")
    void shouldReturnEmptyListWhenNoCoupleTasks() {
        // Given
        when(coupleTaskRepository.findByCoupleIdWithTask(testCouple.getId())).thenReturn(Collections.emptyList());

        // When
        List<TaskResponseDTO> result = taskService.findAllTaskAndSubTask(customUserDetails);

        // Then
        assertThat(result).isNotNull().isEmpty();

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(coupleTaskRepository, times(1)).findByCoupleIdWithTask(testCouple.getId());
    }

    @Test
    @DisplayName("findAllTaskAndSubTask: 사용자를 찾을 수 없을 때 예외를 발생시켜야 한다")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                taskService.findAllTaskAndSubTask(customUserDetails)
        );

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(coupleTaskRepository, never()).findByCoupleIdWithTask(any()); // coupleTaskRepository는 호출되지 않아야 함
    }
}

