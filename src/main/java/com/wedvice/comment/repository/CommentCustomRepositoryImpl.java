package com.wedvice.comment.repository;

import static com.wedvice.comment.entity.QComment.comment;
import static com.wedvice.subtask.entity.QSubTask.subTask;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.comment.entity.Comment;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findAllBySubtaskId(Long subtaskId) {
        return queryFactory.selectFrom(comment)
            .join(comment.subTask, subTask)
            .where(subTask.id.eq(subtaskId))
            .fetch();
    }
}
