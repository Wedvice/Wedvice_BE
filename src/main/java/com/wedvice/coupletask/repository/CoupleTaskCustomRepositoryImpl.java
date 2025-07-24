package com.wedvice.coupletask.repository;

import static com.wedvice.couple.entity.QCouple.couple;
import static com.wedvice.coupletask.entity.QCoupleTask.coupleTask;
import static com.wedvice.subtask.entity.QSubTask.subTask;
import static com.wedvice.task.entity.QTask.task;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.subtask.entity.SubTask;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CoupleTaskCustomRepositoryImpl implements CoupleTaskCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SubTask> findSubTasksForCoupleTaskInfo(Long coupleId) {
        return queryFactory
            .selectFrom(subTask)
            .join(subTask.coupleTask, coupleTask).fetchJoin()
            .join(coupleTask.task, task).fetchJoin()
            .where(coupleTask.couple.id.eq(coupleId))
            .fetch();
    }

    @Override
    public List<CoupleTask> findCoupleTaskWithTaskByCoupleId(Long coupleId) {
        return queryFactory.selectFrom(coupleTask)
            .join(coupleTask.couple, couple)
            .join(coupleTask.task, task).fetchJoin()
            .where(coupleTask.couple.id.eq(coupleId))
            .fetch();
    }
}
