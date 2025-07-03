package com.wedvice.subtask.entity;

import com.wedvice.couple.entity.Couple;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.task.entity.Task;
import com.wedvice.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SubTaskTest {

    private CoupleTask coupleTask;

    @BeforeEach
    void setUp() {
        Couple couple = Couple.create();
        Task task = Task.builder().title("Test Task").build();
        coupleTask = CoupleTask.create(task, couple);
    }

    @Test
    @DisplayName("SubTask 정적 팩토리 메서드 생성 테스트")
    void createSubTaskTest() {
        // Given
        String displayName = "Test SubTask";
        int orders = 1;
        LocalDate targetDate = LocalDate.now();
        User.Role role = User.Role.GROOM;
        Integer price = 10000;
        String content = "Test Content";

        // When
        SubTask subTask = SubTask.create(coupleTask, displayName, orders, targetDate, role, price, content);

        // Then
        assertThat(subTask).isNotNull();
        assertThat(subTask.getCoupleTask()).isEqualTo(coupleTask);
        assertThat(subTask.getDisplayName()).isEqualTo(displayName);
        assertThat(subTask.getOrders()).isEqualTo(orders);
        assertThat(subTask.getTargetDate()).isEqualTo(targetDate);
        assertThat(subTask.getRole()).isEqualTo(role);
        assertThat(subTask.getPrice()).isEqualTo(price);
        assertThat(subTask.getContent()).isEqualTo(content);
        assertThat(subTask.getCompleted()).isFalse();
        assertThat(subTask.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("기본 SubTask 목록 생성 테스트")
    void createDefaultsForTest() {
        // When
        List<SubTask> defaultSubTasks = SubTask.createDefaultsFor(coupleTask);

        // Then
        assertThat(defaultSubTasks).isNotNull();
        assertThat(defaultSubTasks).hasSize(5);

        // 첫 번째 기본 SubTask의 속성 검증 (예시)
        SubTask firstSubTask = defaultSubTasks.get(0);
        assertThat(firstSubTask.getCoupleTask()).isEqualTo(coupleTask);
        assertThat(firstSubTask.getDisplayName()).isEqualTo("첫 데이트 준비");
        assertThat(firstSubTask.getOrders()).isEqualTo(0);
        assertThat(firstSubTask.getRole()).isEqualTo(User.Role.GROOM);
    }

    @Test
    @DisplayName("완료 상태 변경 테스트")
    void updateCompleteStatusTest() {
        // Given
        SubTask subTask = SubTask.create(coupleTask, "Test", 1, LocalDate.now(), User.Role.TOGETHER, 0, "");

        // When & Then
        // false -> true
        assertThat(subTask.getCompleted()).isFalse();
        subTask.updateCompleteStatus();
        assertThat(subTask.getCompleted()).isTrue();

        // true -> false
        subTask.updateCompleteStatus();
        assertThat(subTask.getCompleted()).isFalse();
    }

}
