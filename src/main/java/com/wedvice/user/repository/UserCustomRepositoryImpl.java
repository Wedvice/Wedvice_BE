package com.wedvice.user.repository;

import static com.wedvice.couple.entity.QCouple.couple;
import static com.wedvice.user.entity.QUser.user;
import static com.wedvice.user.entity.QUserConfig.userConfig;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.user.dto.QUserDto;
import com.wedvice.user.dto.UserDto;
import com.wedvice.user.entity.User;
import com.wedvice.user.entity.User.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<UserDto> getAllUserTestExample() {

        List<UserDto> userList = queryFactory.
            select(new QUserDto(user.id, user.nickname, user.profileImageUrl, user.memo
            ))
            .from(user)
            .fetch();

        return userList;
    }

    @Override
    public Optional<User> findByUserWithCoupleAndPartner(Long userId) {
//        QUser partner = new QUser("partner");
        return Optional.ofNullable(queryFactory.selectFrom(user)
            .leftJoin(user.couple, couple).fetchJoin()
//            .leftJoin(couple.users, partner).fetchJoin()
            .where(user.id.eq(userId))
            .fetchOne());
    }

    @Override
    public Long findCoupleIdByUserId(Long userId) {
        return queryFactory
            .select(user.couple.id)
            .from(user)
            .where(user.id.eq(userId))
            .fetchOne();
    }

    @Override
    public Optional<User> findUserWithCoupleAndConfigById(Long userId) {
        User result = queryFactory
            .selectFrom(user)
            .join(user.userConfig, userConfig).fetchJoin()
            .join(user.couple, couple).fetchJoin()
            .where(user.id.eq(userId))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<User> findAllByIsTestTrueAndCoupleIsNullAndCreatedAtBefore(LocalDateTime cutoff) {
        return queryFactory.selectFrom(user)
            .where(user.role.eq(Role.TEST),
                user.couple.isNull(),
                user.createdAt.before(cutoff))
            .fetch();
    }
}
