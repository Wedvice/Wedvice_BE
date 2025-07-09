package com.wedvice.subtask.repository;

import static com.wedvice.coupletask.entity.QCoupleTask.coupleTask;
import static com.wedvice.subtask.entity.QSubTask.subTask;
import static com.wedvice.task.entity.QTask.task;
import static com.wedvice.user.entity.QUser.user;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.subtask.dto.CompleteRateResponseDto;
import com.wedvice.subtask.dto.HomeSubTaskConditionDto;
import com.wedvice.subtask.dto.SubTaskResponseDTO;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.user.entity.User;
import com.wedvice.user.entity.User.Role;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;


@RequiredArgsConstructor
public class SubTaskCustomRepositoryImpl implements SubTaskCustomRepository {

  private final JPAQueryFactory queryFactory;

  public List<SubTaskResponseDTO> getSubTasks(Long userId, Long taskId) {

    // 커플 ID 찾기
    Long coupleId = queryFactory
        .select(user.couple.id)
        .from(user)
        .where(user.id.eq(userId))
        .fetchOne();

//        리포지토리는 db관련 작업만 이 작업은 서비스로 넘기기.
        if (coupleId == null) return List.of();

        // DTO로 뽑기
//        db -> db에서 컬럼 몇개 더 조회해온다고 성능저하가 발생하진 않고
//        join문이나 where절 인덱스 걸러내는거
//        엔티티를 조회해와서 그 연관관계를 쓰다보니 lazy-loadingㅇㅣ 발생하고 fetchjoin으로
//        해결하는데 이거에 대한 또 문제가 있고. 이거를 또 해결하기 위한 .....
//        저는 mybatis도 분석 List<Order> Lazy-loading , eager(X), fetchjoin
        return queryFactory
                .select(Projections.constructor(
                        SubTaskResponseDTO.class,
                        subTask.id,
                        subTask.displayName,
                        subTask.completed,
                        subTask.role.stringValue(),
                        subTask.price,
                        subTask.targetDate,
                        subTask.content,
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
