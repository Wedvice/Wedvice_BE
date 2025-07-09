package com.wedvice.subtask.entity;

import com.wedvice.common.BaseEntity;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubTask extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subtask_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "coupletask_id")
    private CoupleTask coupleTask;

//    정렬 순서 파악을 위한 컬럼. ( 인덱스 0부터 시작)
    private int orders;

    private String displayName;

    @Enumerated(EnumType.STRING)
    private User.Role role;

//    subtask 안에 가격 종류가 여러개 있다.( 이름과 가격이 있음 )
//    엔티티로 빼야하나??
//    priceEntity -> id, price,name ,@ManyToOne SubTask_id, createdDate asc desc든,
    private Integer price;

    private LocalDate targetDate;

    private boolean completed;

    private String content;

    private boolean deleted;



    /**
     * 메서드 시작
     */



    // private 생성자 (빌더 패턴용)
    @Builder
    private SubTask(CoupleTask coupleTask, int orders, String displayName, User.Role role, Integer price, LocalDate targetDate, boolean completed, String content, boolean deleted) {
        this.coupleTask = coupleTask;
        this.orders = orders;
        this.displayName = displayName;
        this.role = role;
        this.price = price;
        this.targetDate = targetDate;
        this.completed = completed;
        this.content = content;
        this.deleted = deleted;
    }

    // 정적 팩토리 메서드
    public static SubTask create(CoupleTask coupleTask, String displayName, int orders,LocalDate targetDate, User.Role role, Integer price, String content) {
        return SubTask.builder()
                .coupleTask(coupleTask)
                .displayName(displayName)
                .orders(orders)
                .role(role)
                .price(price)
                .targetDate(targetDate)
                .content(content)
                .completed(false)
                .deleted(false)
                .build();
    }

    // 비즈니스 로직
    public static List<SubTask> createDefaultsFor(CoupleTask coupleTask) {
        List<SubTask> defaults = new ArrayList<>();

        // 예시 기본 서브태스크 생성
        defaults.add(SubTask.create(coupleTask,"첫 데이트 준비", 0,LocalDate.now(), User.Role.GROOM, 100000, "맛집 탐방하기"));
        defaults.add(SubTask.create(coupleTask,"기념일 선물 준비", 1,LocalDate.now(), User.Role.TOGETHER, 200000, "서로를 위한 선물 고르기"));
        defaults.add(SubTask.create(coupleTask,"여행 계획 세우기", 2,LocalDate.now(), User.Role.TOGETHER, 300000, "함께 갈 여행지 정하기"));
        defaults.add(SubTask.create(coupleTask,"공동 계좌 만들기", 3,LocalDate.now(), User.Role.BRIDE, 400000, "재정 계획 세우기"));
        defaults.add(SubTask.create(coupleTask,"집 꾸미기", 4,LocalDate.now(), User.Role.TOGETHER, 500000, "인테리어 아이디어 모으기"));
        return defaults;
    }

    // 연관관계 편의 메서드
    public void assignToCoupleTask(CoupleTask coupleTask) {
        this.coupleTask = coupleTask;
    }

    public void updateCompleteStatus() {
        this.completed = !this.completed;
    }

    // 조회 메서드
    public boolean getCompleted() {
        return this.completed;
    }

}