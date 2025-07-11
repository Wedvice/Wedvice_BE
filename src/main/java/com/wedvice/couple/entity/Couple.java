package com.wedvice.couple.entity;

import com.wedvice.common.BaseTimeEntity;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.task.entity.Task;
import com.wedvice.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
public class Couple extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "couple_id")
    private Long id;

    @Column(name = "wedding_date", nullable = true)  // 결혼 날짜 nullable
    private LocalDate weddingDate;

    @OneToMany(mappedBy = "couple", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100) // 1:N 최적화
    private List<User> users;

    @OneToMany(mappedBy = "couple", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100) // 1:N 최적화
    private List<CoupleTask> coupleTasks;


    /**
     * 메서드 시작
     */

// Couple.java
//    public void addUser(User user) {
//        this.users.add(user);
//        user.matchCouple(this);
//    }
//
//    public static Couple createWithUsers(User user1, User user2) {
//        if (user1.equals(user2)) {
//            throw new SamePersonMatchException();
//        }
//
//        Couple couple = Couple.builder().build();
//        couple.addUser(user1);
//        couple.addUser(user2);
//        return couple;
//    }

    // protected 생성자 (빌더 패턴용)
    @Builder(access = AccessLevel.PRIVATE)
    protected Couple() {
        this.users = new ArrayList<>();
        this.coupleTasks = new ArrayList<>();
    }

    // 정적 팩토리 메서드
    public static Couple create() {
        return Couple.builder()
            .build();
    }

    // 연관관계 편의 메서드
    public void addCoupleTask(CoupleTask coupleTask) {
        this.coupleTasks.add(coupleTask);
    }

    // 업데이트 메서드
    public void updateWeddingDate(LocalDate weddingDate) {
        this.weddingDate = weddingDate;
    }

    // 비즈니스 메서드
    public void initializeTasks(List<Task> tasks) {
        if (!coupleTasks.isEmpty()) {
            throw new IllegalStateException("Tasks already initialized for this couple");
        }

        tasks.forEach(task -> {
            CoupleTask coupleTask = CoupleTask.create(task, this);

            // 기본 SubTask 생성 책임을 CoupleTask로 위임
            coupleTask.initializeDefaultSubTasks();

            addCoupleTask(coupleTask);
        });
    }


}
