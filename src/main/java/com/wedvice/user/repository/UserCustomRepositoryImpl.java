package com.wedvice.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.user.dto.QUserDto;
import com.wedvice.user.dto.UserDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.wedvice.user.entity.QUser.*;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository{


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
}
