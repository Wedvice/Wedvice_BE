package com.wedvice.couple.entity;

import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.task.entity.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoupleTest {

    @Test
    @DisplayName("Couple 엔티티 생성 테스트")
    void createCoupleTest() {

        // Given , When
        Couple couple = Couple.create();

        // Then
        assertThat(couple).isNotNull();
        assertThat(couple.getUsers()).isNotNull().isEmpty();
        assertThat(couple.getCoupleTasks()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("updateWeddingDate 메서드 테스트")
    void updateWeddingDateTest() {
        // Given
        Couple couple = Couple.create();
        LocalDate newWeddingDate = LocalDate.of(2025, 12, 25);

        // When
        assertThat(couple.getWeddingDate()).isNull();
        couple.updateWeddingDate(newWeddingDate);

        // Then
        assertThat(couple.getWeddingDate()).isEqualTo(newWeddingDate);
    }




    @Test
    @DisplayName("initializeTasks 메서드 테스트 - 초기화 성공")
    void initializeTasks_success() {
        // Given
        Couple couple = Couple.create();
        Task task1 = Task.builder().title("Task 1").build();
        Task task2 = Task.builder().title("Task 2").build();
        List<Task> tasks = Arrays.asList(task1, task2);

        // When
        couple.initializeTasks(tasks);

        // Then
        assertThat(couple.getCoupleTasks()).hasSize(2);
        CoupleTask coupleTask1 = couple.getCoupleTasks().get(0);
        assertThat(coupleTask1.getCouple()).isEqualTo(couple);
        assertThat(coupleTask1.getTask()).isEqualTo(task1);
        assertThat(coupleTask1.isDeleted()).isFalse();

        CoupleTask coupleTask2 = couple.getCoupleTasks().get(1);
        assertThat(coupleTask2.getCouple()).isEqualTo(couple);
        assertThat(coupleTask2.getTask()).isEqualTo(task2);
        assertThat(coupleTask2.isDeleted()).isFalse();
    }




    @Test
    @DisplayName("initializeTasks 메서드 테스트 - 이미 초기화된 경우 예외 발생")
    void initializeTasks_alreadyInitialized_throwsException() {
        // Given
        Couple couple = Couple.create();
        Task task1 = Task.builder().title("Task 1").build();
        couple.initializeTasks(Collections.singletonList(task1)); // 첫 번째 초기화

        // When & Then
        assertThatThrownBy(() -> couple.initializeTasks(Collections.singletonList(Task.builder().title("New Task").build())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Tasks already initialized for this couple");
    }

    @Test
    @DisplayName("addCoupleTask 메서드 테스트")
    void addCoupleTaskTest() {
        // Given
        Couple couple = Couple.create();
        Task task = Task.builder().title("Test Task").build();
        CoupleTask coupleTask = CoupleTask.create(task, couple);

        // When
        couple.addCoupleTask(coupleTask);

        // Then
        assertThat(couple.getCoupleTasks()).hasSize(1);
        assertThat(couple.getCoupleTasks().get(0)).isEqualTo(coupleTask);
        assertThat(coupleTask.getCouple()).isEqualTo(couple);
    }
}
