package com.wedvice.comment.repository;

import com.wedvice.comment.entity.Comment;
import java.util.List;

public interface CommentCustomRepository {

    List<Comment> findAllBySubtaskId(Long subtaskId);
}
