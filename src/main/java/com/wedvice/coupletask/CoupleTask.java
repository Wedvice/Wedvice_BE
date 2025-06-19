package com.wedvice.coupletask;

import com.wedvice.common.BaseEntity;
import com.wedvice.couple.entity.Couple;
import com.wedvice.task.Task;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CoupleTask extends BaseEntity {


//    커플테스크는 커플이 매칭됨과 동시에 생성되어야한다.
//    기본 포맷이 존재.

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


    private boolean deleted;

}
