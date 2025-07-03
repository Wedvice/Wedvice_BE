package com.wedvice;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wedvice.user.entity.QUser.user;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class WedviceApplicationTests {

    @Autowired
    EntityManager em;

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    UserRepository userRepository;


    @Test
    public void testMember() throws Exception{

        User kakaoUser = User.create("12412231","kakao");
        User anotherUser = User.create("1234444","naver");


        //when
        userRepository.save(kakaoUser);
        userRepository.save(anotherUser);

        em.flush();
        em.clear();

        User findUser = queryFactory
                .select(user)
                .from(user)
                .where(user.provider.eq("kakao"))
                .fetchOne();

        List<User> userList = queryFactory
                .select(user)
                .from(user)
                .fetch();


        //then
        assertThat(findUser.getProvider()).isEqualTo("kakao");
        assertThat(findUser.getId()).isEqualTo(kakaoUser.getId());
        assertThat(findUser.getProvider()).isNotEqualTo("naver");
        assertThat(userList.size()).isEqualTo(2);

    }
}
