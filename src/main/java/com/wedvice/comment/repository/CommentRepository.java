package com.wedvice.comment.repository;

import com.wedvice.comment.entity.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    Optional<Comment> findByIdAndDeletedFalse(Long commentId);
}
