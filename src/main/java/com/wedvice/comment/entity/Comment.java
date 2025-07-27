package com.wedvice.comment.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.wedvice.common.BaseEntity;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;

@Entity
@Builder(access = AccessLevel.PRIVATE)
public class Comment extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subtask_id", nullable = false)
    private SubTask subTask;

    //    컨텐츠 안에 사진이 위치해야하는데 에디터?툴을 써야하는건가요? 사진은 한 댓글에 총 9개까지 가능
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 정적 팩토리 메서드
    public static Comment create(User user, SubTask subTask, String content) {
        return Comment.builder()
            .user(user)
            .subTask(subTask)
            .content(content)
            .build();
    }

    public String getUserNickname() {
        return user.getNickname();
    }
}
