package com.wedvice.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.user.entity.User;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserCustomRepositoryImpl 통합 테스트")
class UserCustomRepositoryImplTest {


    @Autowired
    private UserCustomRepositoryImpl userCustomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoupleRepository coupleRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private EntityManager entityManager;


    @Test
    @DisplayName("partner에 대한 쿼리 확인")
    public void findByUserWithCoupleAndPartner_쿼리_테스트() throws Exception {

        //given

        Couple couple = Couple.create();

        coupleRepository.save(couple);

        User userA = User.create("123456", "naver");
        User userB = User.create("654321", "kakao");
        User userC = User.create("654322", "kakao22");
        userA.updateNickname("11");
        userB.updateNickname("22");
        userC.updateNickname("33");

        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);

        userA.matchCouple(couple);
        userB.matchCouple(couple);
        userC.matchCouple(couple);

        entityManager.flush();
        entityManager.clear();

        //when
        User findUser = userCustomRepository.findByUserWithCoupleAndPartner(userA.getId()).get();

        //then

        findUser.getCouple().getUsers().forEach(i -> System.out.println(i.getNickname()));


    }

    @Test
    @DisplayName("batchSize in절 쿼리가 나가야한다.")
    public void 커플_getUsers_BatchSizeTest() throws Exception {

        //given

        Couple couple = Couple.create();
        Couple couple1 = Couple.create();

        coupleRepository.save(couple);

        coupleRepository.save(couple1);

        User userA = User.create("123456", "naver");
        User userB = User.create("654321", "kakao");
        User userC = User.create("654322", "kakao22");
        userA.updateNickname("11");
        userB.updateNickname("22");
        userC.updateNickname("33");

        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);

        userA.matchCouple(couple);
        userB.matchCouple(couple);
        userC.matchCouple(couple);

        entityManager.flush();
        entityManager.clear();

        //when

        List<Couple> couples = coupleRepository.findAll();

        for (Couple cou : couples) {
            System.out.println(cou.getUsers().size());
        }

//        users
//            from
//            where couple_id in ( 1,2 )

        //then
//        Hibernate: -> batchSize 키고 한것.
//        select
//        u1_0.couple_id,
//            u1_0.user_id,
//            u1_0.created_at,
//            u1_0.email,
//            u1_0.memo,
//            u1_0.nickname,
//            u1_0.oauth_id,
//            u1_0.profile_image_url,
//            u1_0.provider,
//            u1_0.refresh_token,
//            u1_0.role,
//            u1_0.updated_at
//        from
//        users u1_0
//        where
//        u1_0.couple_id=?

    }

    @Test
    public void NPlus1Test() throws Exception {
        //given

        Couple coupleA = Couple.create();
        Couple coupleB = Couple.create();

        coupleRepository.save(coupleA);

        coupleRepository.save(coupleB);

        User userA = User.create("123456", "naver");
        User userB = User.create("654321", "kakao");
        User userC = User.create("654322", "kakao22");
        userA.updateNickname("11");
        userB.updateNickname("22");
        userC.updateNickname("33");

        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);

        userA.matchCouple(coupleA);
        userB.matchCouple(coupleA);
        userC.matchCouple(coupleB);

        entityManager.flush();
        entityManager.clear();

        //when

        List<Couple> couples = coupleRepository.findAll();

//        couple는 2개
        for (Couple couple : couples) {

//            coupleA에는 2개
//            coupleB에는 1개
            List<User> users = couple.getUsers();

            for (User user : users) {

                System.out.println("user.getNickname() = " + user.getNickname());
            }

        }

        //then

    }


}