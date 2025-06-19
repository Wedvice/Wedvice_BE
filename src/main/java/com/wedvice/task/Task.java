package com.wedvice.task;

import com.wedvice.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Task extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;


    @Column(nullable = false)
    private String title;



}
