package com.wedvice.task.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.couple.entity.QCouple;
import com.wedvice.coupletask.entity.QCoupleTask;
import com.wedvice.subtask.entity.QSubTask;
import com.wedvice.task.dto.TaskResponseDTO;
import com.wedvice.task.entity.QTask;
import com.wedvice.user.entity.QUser;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.wedvice.coupletask.entity.QCoupleTask.*;
import static com.wedvice.subtask.entity.QSubTask.subTask;
import static com.wedvice.task.entity.QTask.*;
import static com.wedvice.user.entity.QUser.*;

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
