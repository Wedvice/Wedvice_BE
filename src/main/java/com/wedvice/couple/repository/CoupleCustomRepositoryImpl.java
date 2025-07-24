package com.wedvice.couple.repository;

import static com.wedvice.couple.entity.QCouple.couple;
import static com.wedvice.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.couple.entity.Couple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CoupleCustomRepositoryImpl implements CoupleCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Couple findCoupleByUserId(Long userId) {
        return queryFactory.selectFrom(couple)
            .join(couple.users, user)
            .where(user.id.eq(userId))
            .fetchOne();
    }
}
