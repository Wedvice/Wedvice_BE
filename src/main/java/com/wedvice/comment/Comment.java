package com.wedvice.comment;

import com.wedvice.user.entity.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
public class Comment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;



//    댓글 입력시 유저를 자동으로 넣어주게 하는 jpa 기능이 있음.
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
