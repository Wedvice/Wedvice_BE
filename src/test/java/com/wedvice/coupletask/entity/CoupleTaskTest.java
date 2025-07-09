package com.wedvice.coupletask.entity;

import com.wedvice.couple.entity.Couple;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.task.entity.Task;
import com.wedvice.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CoupleTaskTest {

    private Couple couple;
    private Task task;

    @BeforeEach
    void setUp() {
        couple = Couple.create();
        task = Task.builder().title("Test Task").build();
    }

    @Test
    @DisplayName("CoupleTask 정적 팩토리 메서드 생성 테스트")
    void createCoupleTaskTest() {
        // Given & When
        CoupleTask coupleTask = CoupleTask.create(task, couple);

        // Then
        assertThat(coupleTask).isNotNull();
        assertThat(coupleTask.getTask()).isEqualTo(task);
        assertThat(coupleTask.getCouple()).isEqualTo(couple);
        assertThat(coupleTask.isDeleted()).isFalse();
        assertThat(coupleTask.getSubTasks()).isNotNull().isEmpty();
        assertThat(coupleTask.getSubTasks().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("기본 SubTask 초기화 테스트")
    void initializeDefaultSubTasksTest() {
        // Given
        CoupleTask coupleTask = CoupleTask.create(task, couple);
        int expectedDefaultSubTaskCount = 5; // SubTask.createDefaultsFor가 생성하는 개수

        // When
        coupleTask.initializeDefaultSubTasks();

        // Then
        assertThat(coupleTask.getSubTasks()).hasSize(expectedDefaultSubTaskCount);
        
        // 각 SubTask가 coupleTask와 잘 연결되었는지 확인
        for (SubTask subTask : coupleTask.getSubTasks()) {
            assertThat(subTask.getCoupleTask()).isEqualTo(coupleTask);
        }
    }

    @Test
    @DisplayName("삭제 상태 변경 테스트")
    void updateDeleteStatusTest() {
        // Given
        CoupleTask coupleTask = CoupleTask.create(task, couple);

        // When
        assertThat(coupleTask.isDeleted()).isFalse(); // 초기 상태 확인
        coupleTask.updateDeleteStatus();

        // Then
        assertThat(coupleTask.isDeleted()).isTrue();
    }

    
    @Test
    @DisplayName("addSubTask 메서드 테스트")
    void addSubTaskTest() {
        // Given
        CoupleTask coupleTask = CoupleTask.create(task, couple);
        SubTask newSubTask = SubTask.create(coupleTask, "New SubTask", 0, LocalDate.now(), User.Role.TOGETHER, 0, "");

        // When
        coupleTask.addSubTask(newSubTask);

        // Then
        assertThat(coupleTask.getSubTasks()).hasSize(1);
        assertThat(coupleTask.getSubTasks().get(0)).isEqualTo(newSubTask);
    }

    @Test
    @DisplayName("CoupleTask의 updateDeleteStatus가 호출되면 deleted 상태가 변경되어야 한다")
    void coupleTaskUpdateDeleteStatusShouldChangeDeletedState() {
        // Given
        // CoupleTask.create() 정적 팩토리 메서드를 사용하여 인스턴스 생성
        CoupleTask coupleTask = CoupleTask.create(mock(com.wedvice.task.entity.Task.class), mock(Couple.class));
        assertFalse(coupleTask.isDeleted()); // 초기 상태는 false

        // When
        coupleTask.updateDeleteStatus();

        // Then
        assertTrue(coupleTask.isDeleted()); // 상태가 true로 변경되어야 함
    }
}
