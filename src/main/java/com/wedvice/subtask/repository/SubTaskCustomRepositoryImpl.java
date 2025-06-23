package com.wedvice.subtask.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.subtask.dto.SubTaskResponseDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.wedvice.coupletask.entity.QCoupleTask.coupleTask;
import static com.wedvice.subtask.entity.QSubTask.subTask;
import static com.wedvice.task.entity.QTask.task;
import static com.wedvice.user.entity.QUser.user;


@RequiredArgsConstructor
public class SubTaskCustomRepositoryImpl implements SubTaskCustomRepository{

    private final JPAQueryFactory queryFactory;

    public List<SubTaskResponseDTO> getSubTasks(Long userId, Long taskId) {

        // 커플 ID 찾기
        Long coupleId = queryFactory
                .select(user.couple.id)
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();

        if (coupleId == null) return List.of();

        // DTO로 뽑기
        return queryFactory
                .select(Projections.constructor(
                        SubTaskResponseDTO.class,
                        subTask.id,
                        subTask.displayName,
                        subTask.completed,
                        subTask.role.stringValue(),
                        subTask.price,
                        subTask.targetDate,
                        subTask.contents,
                        subTask.orders
                ))
                .from(subTask)
                .join(subTask.coupleTask, coupleTask)
                .join(coupleTask.task, task)
                .where(
                        coupleTask.couple.id.eq(coupleId),
                        coupleTask.task.id.eq(taskId),
                        coupleTask.deleted.eq(false)
                )
                .orderBy(subTask.orders.asc())
                .fetch();
    }
}
