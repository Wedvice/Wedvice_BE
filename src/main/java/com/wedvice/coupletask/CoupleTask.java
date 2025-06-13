package com.wedvice.coupletask;

import com.wedvice.common.BaseEntity;
import com.wedvice.couple.entity.Couple;
import com.wedvice.task.Task;
import jakarta.persistence.*;

@Entity
public class CoupleTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupletask_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "couple_id")
    private Couple couple;


    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

}
