package com.wedvice.subtask.entity;

import com.wedvice.common.BaseEntity;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SubTask extends BaseEntity{

//    서브테스크도 기본포맷 1개정도는 필요할듯, 페이징 처리 , 하위태스크 추가

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

    private Integer price;

    private String contents;

    private LocalDate targetDate;

    private boolean completed;

    private String content;

    @Transient
    private boolean deleted;

}