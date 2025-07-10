
package com.wedvice.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wedvice.common.config.QuerydslConfig;
import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@DisplayName("User 엔티티 통합 테스트")
@Import(QuerydslConfig.class)
class UserRepositoryTest {

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
        newUser.updateNickname("테스"); // 2자 닉네임으로 수정
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
        assertThat(foundUser.getNickname()).isEqualTo("테스");
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
    @DisplayName("Nickname 길이 초과 시 DataIntegrityViolationException 발생")
    void nickname_LengthExceeds_ThrowsException() {
        // Given
        User user = User.createForTest("longNicknameOauthId", "kakao", "긴닉네임", null); // 3자 닉네임 (DB 제약조건 위반)

        // When & Then
        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(user);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Memo 길이 초과 시 DataIntegrityViolationException 발생")
    void memo_LengthExceeds_ThrowsException() {
        // Given
        User user = User.createForTest("longMemoOauthId", "kakao", "테스", "이것은18자를초과하는매우긴메모입니다."); // 19자 메모 (DB 제약조건 위반)

        // When & Then
        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(user);
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
        newUser.updateNickname("커플"); // 2자 닉네임으로 수정
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

    @Test
    @DisplayName("findByOauthId: oauthId로 User를 정상적으로 조회해야 한다.")
    void findByOauthId_UserEntity_Success() {
        // Given
        String oauthId = "find-by-oauth-id";
        User user = User.create(oauthId, "kakao");
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByOauthId(oauthId);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getOauthId()).isEqualTo(oauthId);
    }

    @Test
    @DisplayName("User 삭제: delete()가 정상 동작해야 한다.")
    void delete_UserEntity_Success() {
        // Given
        User user = User.create("delete-oauth-id", "kakao");
        User savedUser = userRepository.save(user);

        // When
        userRepository.delete(savedUser);
        userRepository.flush(); // 즉시 DB에 반영

        // Then
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("User 업데이트: 필드 변경 후 저장 시 정상적으로 반영되어야 한다.")
    void updateUserFields_Success() {
        // Given
        User user = User.create("update-oauth-id", "google");
        User savedUser = userRepository.save(user);

        // When
        savedUser.updateNickname("새닉");
        savedUser.updateProfileImage("http://new.image.com/new.jpg");
        savedUser.updateMemo("새로운 메모");
        savedUser.updateRefreshToken("newRefreshToken");
        savedUser.updateEmail("new.email@example.com");
        savedUser.updateRole(User.Role.GROOM);

        userRepository.flush(); // 변경사항 즉시 DB 반영

        // Then
        User foundUser = userRepository.findById(savedUser.getId()).orElseThrow();

        assertThat(foundUser.getNickname()).isEqualTo("새닉");
        assertThat(foundUser.getProfileImageUrl()).isEqualTo("http://new.image.com/new.jpg");
        assertThat(foundUser.getMemo()).isEqualTo("새로운 메모");
        assertThat(foundUser.getRefreshToken()).isEqualTo("newRefreshToken");
        assertThat(foundUser.getEmail()).isEqualTo("new.email@example.com");
        assertThat(foundUser.getRole()).isEqualTo(User.Role.GROOM);
    }
}
