package com.wedvice.subtask.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.wedvice.common.config.QuerydslConfig;
import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.subtask.dto.CompleteRateResponseDto;
import com.wedvice.task.repository.TaskRepository;
import com.wedvice.user.common.UserTestFixture;
import com.wedvice.user.entity.User;
import com.wedvice.user.entity.User.Role;
import com.wedvice.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("SubTaskCustomRepositoryImpl 통합 테스트")
@Sql(scripts = "/metadata.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SubTaskCustomRepositoryImplTest {

    @Autowired
    EntityManager em;

    @Autowired
    SubTaskCustomRepositoryImpl sr;

    @Autowired
    UserRepository ur;
    @Autowired
    TaskRepository tr;

    @Autowired
    CoupleRepository cr;
    User groom;
    User bride;
    Long groomId;
    Long brideId;
    Couple couple;
    Long coupleId;

    @BeforeEach
    void setup() {
        // 1. 커플 및 유저 생성
        couple = cr.save(Couple.create());
        groom = ur.save(UserTestFixture.createWithRoleAndNickName(Role.GROOM, "신랑"));
        bride = ur.save(UserTestFixture.createWithRoleAndNickName(Role.BRIDE, "신부"));

        // 2. 연관관계 설정
        groom.matchCouple(couple);
        bride.matchCouple(couple);

        // 3. 연관관계 반영 및 flush
        for (var e : tr.findAll()) {
            couple.getCoupleTasks().add(CoupleTask.create(e, couple));
        }
        em.flush();

        // 4. ID 보관
        coupleId = couple.getId();
        groomId = groom.getId();
        brideId = bride.getId();
    }

    @Nested
    @DisplayName("findHomeSubTasksByCondition 함수 테스트")
    class findHomeSubTasksByCondition {

        @Test
        void test() {
        }
    }

    @Nested
    @DisplayName("getProgressRate 함수 테스트")
    class getProgressRate {

        @Test
        @DisplayName("서브테스크를 생성하지 않으면 0을 반환한다")
        void returnZeroIfNotExistSubtask() {
            // given
            couple = cr.getReferenceById(coupleId);

            CompleteRateResponseDto dto = sr.getProgressRate(coupleId, null);
            for (var e : couple.getCoupleTasks()) {
                System.out.println(e.getTask().getTitle() + " : ");
                for (var el : e.getSubTasks()) {
                    System.out.println(el.getContent());
                }
            }
            // then
            assertThat(dto.getCompleted()).isEqualTo(0);
            assertThat(dto.getPercent()).isEqualTo(0);
            assertThat(dto.getTotal()).isEqualTo(0);
        }
    }
}