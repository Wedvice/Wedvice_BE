package com.wedvice.task.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.task.dto.TaskResponseDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.wedvice.coupletask.entity.QCoupleTask.coupleTask;
import static com.wedvice.subtask.entity.QSubTask.subTask;
import static com.wedvice.task.entity.QTask.task;
import static com.wedvice.user.entity.QUser.user;

@RequiredArgsConstructor
public class CustomTaskRepositoryImpl implements CustomTaskRepository{

    private final JPAQueryFactory queryFactory;





    @Override
    public List<TaskResponseDTO> getAllTaskAndSubTask(Long userId) {

        // 유저의 커플 ID 조회
        Long coupleId = queryFactory
                .select(user.couple.id)
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();

        if (coupleId == null) return List.of();

        return queryFactory
                .select(Projections.constructor(
                        TaskResponseDTO.class,
                        task.id,
//                        커플태스크로 고치기.
                        task.title,
                        subTask.count(),                                       // 전체 서브태스크 수
//                        subTask.count().filter(subTask.completed.isTrue())     // 완료된 수
                        new CaseBuilder()
                                .when(subTask.completed.isTrue())
                                .then(1)
                                .otherwise(0)
                                .sum()    // 완료된 수
                ))
                .from(coupleTask)
                .join(coupleTask.task, task)
                .leftJoin(subTask).on(subTask.coupleTask.eq(coupleTask))
                .where(coupleTask.couple.id.eq(coupleId), coupleTask.deleted.eq(false))
                .groupBy(task.id)
                .fetch();
    }



}
