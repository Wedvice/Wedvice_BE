package com.wedvice.comment.entity;

import com.wedvice.common.BaseEntity;
import com.wedvice.subtask.entity.SubTask;
import jakarta.persistence.*;

import static jakarta.persistence.FetchType.*;

@Entity
public class Comment extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subtask_id")
    private SubTask subTask;

//    컨텐츠 안에 사진이 위치해야하는데 에디터?툴을 써야하는건가요? 사진은 한 댓글에 총 9개까지 가능
    private String content;




}
