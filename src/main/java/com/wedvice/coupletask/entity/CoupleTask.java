package com.wedvice.coupletask.entity;

import com.wedvice.common.BaseEntity;
import com.wedvice.couple.entity.Couple;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.task.entity.Task;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoupleTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupletask_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "couple_id")
    private Couple couple;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @OneToMany(mappedBy = "coupleTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100) // 1:N 최적화
    private List<SubTask> subTasks;

    private boolean deleted;


    /**
     * 메서드 시작
     */


    // private 생성자 (빌더 패턴용)
    @Builder(access = AccessLevel.PRIVATE)
    private CoupleTask(Couple couple, Task task) {
        this.couple = couple;
        this.task = task;
        this.subTasks = new ArrayList<>();
        this.deleted = false;
    }

    // 정적 팩토리 메서드
    public static CoupleTask create(Task task, Couple couple) {
        return CoupleTask.builder()
                .task(task)
                .couple(couple)
                .build();
    }

    // 연관관계 메서드
    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    //업데이트 메서드
    public void updateDeleteStatus(){
        this.deleted = true;
    }

    // 비즈니스 메서드
    public void initializeDefaultSubTasks() {
        List<SubTask> defaultSubTasks = SubTask.createDefaultsFor(this);
        defaultSubTasks.forEach(this::addSubTask);
    }

}
