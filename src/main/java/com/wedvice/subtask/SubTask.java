package com.wedvice.subtask;

import com.wedvice.common.BaseEntity;
import com.wedvice.coupletask.CoupleTask;
import com.wedvice.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class SubTask extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subtask_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "coupletask_id")
    private CoupleTask coupleTask;

//    정렬 순서 파악을 위한 컬럼. ( 인덱스 0부터 시작)
    private int orders;

    private String displayName;

    private User.Role role;

    private Integer price;

    private String contents;

    private LocalDate targetDate;

    private boolean completed;

    private String content;

    @Transient
    private boolean deleted;

}