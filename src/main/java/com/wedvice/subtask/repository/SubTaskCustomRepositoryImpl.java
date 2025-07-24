package com.wedvice.subtask.repository;

import static com.wedvice.coupletask.entity.QCoupleTask.coupleTask;
import static com.wedvice.subtask.entity.QSubTask.subTask;
import static com.wedvice.task.entity.QTask.task;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.subtask.dto.CompleteRateResponseDto;
import com.wedvice.subtask.dto.HomeSubTaskConditionDto;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.user.entity.User;
import com.wedvice.user.entity.User.Role;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;


@RequiredArgsConstructor
public class SubTaskCustomRepositoryImpl implements SubTaskCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<SubTask> getSubTasks(Long userId, Long taskId, Long coupleId) {

        return queryFactory
            .select(subTask)

            .from(subTask)
            .join(subTask.coupleTask, coupleTask).fetchJoin()
            .where(
                coupleTask.couple.id.eq(coupleId),
                coupleTask.task.id.eq(taskId),
                coupleTask.deleted.eq(false)
            )
            .orderBy(subTask.orders.asc())
            .fetch();
    }

    @Override
    public List<SubTask> findHomeSubTasksByCondition(
        HomeSubTaskConditionDto homeSubTaskConditionDto) {
        Long coupleId = homeSubTaskConditionDto.getCoupleId();
        Boolean completed = homeSubTaskConditionDto.getCompleted();
        boolean top3 = homeSubTaskConditionDto.isTop3();
        User.Role role = homeSubTaskConditionDto.getRole();
        String sortType = homeSubTaskConditionDto.getSort();
        Pageable pageable = homeSubTaskConditionDto.getPageable();

        // 2. SubTask 데이터 조회 (Entity fetch → DTO 변환)
        return queryFactory
            .selectFrom(subTask)
            .join(subTask.coupleTask, coupleTask)
            .join(coupleTask.task, task)
            .where(
                coupleTask.couple.id.eq(coupleId),
                completedEq(completed),
                roleEq(role)
            )
            .orderBy(getOrderSpecifiers(sortType, top3))
            .offset(top3 ? 0 : pageable.getOffset())
            .limit(top3 ? 3 : pageable.getPageSize() + 1)
            .fetch();
    }

    @Override
    public CompleteRateResponseDto getProgressRate(Long coupleId, Role filterRole) {
        BooleanExpression idCondition = coupleTask.couple.id.eq(coupleId);
        BooleanExpression roleCondition = roleEq(filterRole);
        // 전체 SubTask 개수
        Long total = queryFactory
            .select(subTask.count())
            .from(subTask)
            .join(subTask.coupleTask, coupleTask)
            .where(idCondition, roleCondition)
            .fetchOne();

        if (total == null || total == 0) {
            return new CompleteRateResponseDto(0, 0, 0);
        }

        // 완료된 SubTask 개수
        Long completed = queryFactory
            .select(subTask.count())
            .from(subTask)
            .join(subTask.coupleTask, coupleTask)
            .where(
                idCondition,
                roleCondition,
                subTask.completed.isTrue()
            )
            .fetchOne();

        int percent = (int) Math.round((completed * 100.0) / total);

        return new CompleteRateResponseDto(percent, completed, total);
    }

    @Override
    public List<SubTask> getSubTasksByDate(Long coupleId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return queryFactory.selectFrom(subTask)
            .join(subTask.coupleTask, coupleTask).fetchJoin()
            .join(coupleTask.task, task).fetchJoin()
            .where(
                subTask.coupleTask.couple.id.eq(coupleId),
                subTask.targetDate.isNotNull(),
                subTask.targetDate.between(start, end))
            .orderBy(subTask.targetDate.asc(), subTask.id.asc())
            .fetch();
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(String sortType, boolean top3) {
        if (top3) {
            return new OrderSpecifier[]{
                subTask.targetDate.asc().nullsLast(),
                task.id.asc(),
                subTask.orders.asc()
            };
        }

        if ("category".equalsIgnoreCase(sortType)) {
            return new OrderSpecifier[]{
                task.id.asc(),
                subTask.completed.asc(), // false(미완료) → true(완료)
                subTask.orders.asc()
            };
        }

        if ("date".equalsIgnoreCase(sortType)) {
            return new OrderSpecifier[]{
                subTask.completed.asc(),
                subTask.targetDate.asc().nullsLast()
            };
        }

        // 기본 정렬
        return new OrderSpecifier[]{
            subTask.createdAt.desc()
        };
    }

    private BooleanExpression roleEq(User.Role role) {
        return role != null ? subTask.role.eq(role) : null;
    }

    private BooleanExpression completedEq(Boolean completed) {
        return completed != null ? subTask.completed.eq(completed) : null;
    }
}
