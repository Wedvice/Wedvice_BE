
package com.wedvice.user.repository;

import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserRepository , 엔티티 데이터 계층 테스트")
class UserPersistenceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoupleRepository coupleRepository;

    @Test
    @DisplayName("User 저장 및 조회: save()와 findById()가 정상 동작해야 한다.")
    void saveAndFind_UserEntity_Success() {
        // Given (준비)
        // 테스트에 사용할 User 엔티티를 생성합니다.
        // User.create() 팩토리 메서드의 동작은 UserTest(단위 테스트)에서 이미 검증되었습니다.
        User newUser = User.create("test-oauth-12345", "kakao");
        newUser.updateNickname("테스트유저");
        newUser.updateProfileImage("http://test.com/image.jpg");
        newUser.updateRole(User.Role.BRIDE);

        // When (실행)
        // 1. userRepository.save(): User 엔티티를 DB에 저장(INSERT)하고, 저장된 엔티티를 반환합니다.
        User savedUser = userRepository.save(newUser);
        // 2. userRepository.findById(): 저장된 ID로 DB에서 User를 다시 조회합니다.
        Optional<User> foundUserOptional = userRepository.findById(savedUser.getId());

        // Then (검증)
        // 조회된 데이터가 우리가 처음에 설정한 데이터와 일치하는지 확인합니다.
        // 이 검증이 통과하면, UserRepository의 기본 동작과 User 엔티티의 DB 매핑이 모두 올바르다는 것을 의미합니다.
        assertThat(foundUserOptional).isPresent(); // Optional이 비어있지 않은지 확인
        User foundUser = foundUserOptional.get();

        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getOauthId()).isEqualTo("test-oauth-12345");
        assertThat(foundUser.getProvider()).isEqualTo("kakao");
        assertThat(foundUser.getNickname()).isEqualTo("테스트유저");
        assertThat(foundUser.getProfileImageUrl()).isEqualTo("http://test.com/image.jpg");
        assertThat(foundUser.getRole()).isEqualTo(User.Role.BRIDE);
    }

    @Test
    @DisplayName("Unique 제약조건: 중복된 oauthId로 저장 시도 시 예외가 발생해야 한다.")
    void oauthId_ShouldBeUnique() {
        // Given (준비)
        // 동일한 oauthId를 가진 두 개의 User 엔티티를 준비합니다.
        User user1 = User.create("same-oauth-id", "kakao");
        User user2 = User.create("same-oauth-id", "naver");

        // 첫 번째 유저는 정상적으로 저장되어야 합니다.
        userRepository.saveAndFlush(user1);

        // When & Then (실행 및 검증)
        // 두 번째 유저를 저장하려고 할 때, DB의 unique 제약조건 위반으로 예외가 발생하는지 검증합니다.
        // saveAndFlush()를 사용하여 즉시 DB에 INSERT 쿼리를 보내 예외 발생 시점을 명확히 합니다.
        // 만약 User 엔티티의 oauthId 필드에 @Column(unique=true) 설정이 없다면 이 테스트는 실패합니다.
        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(user2);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Nickname 길이 초과 시 (11자) 닉네임이 잘려서 저장되어야 한다.")
    void nickname_LengthExceeds_ShouldBeTruncated() {
        // Given
        User newUser = User.create("test-oauth-length-11", "kakao");
        // 10자를 살짝 초과하는 닉네임 (11자)
        String longNickname = "열한글자닉네임입니다1"; // 11자
        newUser.updateNickname(longNickname);


        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(newUser);
        }).isInstanceOf(DataIntegrityViolationException.class);


    }

    @Test
    @DisplayName("Nickname 길이 초과 시 (15자) 예외가 발생해야 한다.")
    void nickname_LengthExceeds_ThrowsException() {
        // Given
        User newUser = User.create("test-oauth-length-15", "kakao");
        // 10자를 크게 초과하는 닉네임 (15자)
        newUser.updateNickname("열다섯자닉네임입니다2345"); // 15자

        // When & Then
        // 닉네임 길이 제약조건 위반으로 예외가 발생하는지 검증합니다.
        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(newUser);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Couple 연관관계 매핑이 정상적으로 동작해야 한다.")
    void couple_Association_WorksCorrectly() {
        // Given
        // Couple 엔티티 생성 및 저장
        Couple newCouple = Couple.create();
        Couple savedCouple = coupleRepository.save(newCouple);

        // User 엔티티 생성 및 Couple 할당
        User newUser = User.create("test-oauth-couple", "kakao");
        newUser.updateNickname("커플유저");
        newUser.matchCouple(savedCouple);

        // When
        // User 저장
        User savedUser = userRepository.save(newUser);

        // User를 다시 조회 (영속성 컨텍스트 초기화 후)
        Optional<User> foundUserOptional = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUserOptional).isPresent();
        User foundUser = foundUserOptional.get();

        // Couple 연관관계가 올바르게 로드되었는지 검증
        assertThat(foundUser.getCouple()).isNotNull();
        assertThat(foundUser.getCouple().getId()).isEqualTo(savedCouple.getId());
    }
}
