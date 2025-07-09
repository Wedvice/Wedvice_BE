package com.wedvice.coupletask.repository;

import static com.wedvice.coupletask.entity.QCoupleTask.coupleTask;
import static com.wedvice.subtask.entity.QSubTask.subTask;
import static com.wedvice.task.entity.QTask.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.task.entity.QTask;
import java.util.List;
import java.util.stream.Collectors;
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
}
