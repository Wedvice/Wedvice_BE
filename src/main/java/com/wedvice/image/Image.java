package com.wedvice.image;

import com.wedvice.comment.Comment;
import jakarta.persistence.*;

import static jakarta.persistence.FetchType.*;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String url;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;



}
