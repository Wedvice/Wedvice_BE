<h1 align="center">
  <br>
  <img src="https://github.com/user-attachments/assets/fc4eee91-ae47-4e53-92c6-81c0c6a6c57b" alt="Wedy logo" width="200" />
  <p>
  Wedy
  <p>
</h1>
<h4 align="center">예비 신랑 신부를 위한 일정 관리 & 커플 플래너</h4>

## 📘 프로젝트 개요

> 커플이 함께 일정과 태스크를 관리하고, 결혼 준비 과정을 공유할 수 있는 **플래너 서비스**입니다.

**개발 기간**: 2025.6.15 ~ 2025.07.30 (6주, 프로토타입 단계 종료)

**핵심 구현**: 커플 매칭 / 일정 등록 / ToDo 관리

## ⚙️ 아키텍처 및 도메인 설계

### 🧩 Main Domain Flow

           Couple ---- CoupleTask ---- Task
             ㅣ             ㅣ
             ㅣ             ㅣ
            User          Task

- **Couple**: 두 명의 User를 연결하는 핵심 엔티티
    - 초대 코드(inviteCode)로 생성 및 매칭
    - 커플 단위로 Task와 SubTask가 관리됨

- **User**: 개인 계정으로, 하나의 Couple에 속함
    - SubTask의 담당자로 참여 가능

- **CoupleTask**: 커플이 함께 관리하는 일정 단위
    - Couple에 속하며 여러 SubTask를 가짐
    - 커플의 실제 협업 단위

- **SubTask**: 세부 업무 단위
    - 특정 CoupleTask에 종속
    - 특정 User가 담당 가능

- **Task (옵션)**: 반복 생성되는 태스크의 템플릿/기준 모델

## 🧱 코드 구조 예시

### 💡 Service - 엔티티 및 리포지토리 호출 및 조합 역할

```java

@Transactional
public void updateWeddingDate(Long userId, LocalDate newWeddingDate) {
    User user = userRepository.findByUserWithCoupleAndPartner(userId)
        .orElseThrow(InvalidUserAccessException::new);

    if (!user.isMatched()) {
        throw new NotMatchedYetException();
    }

    Couple couple = user.getCouple();
    couple.updateWeddingDate(newWeddingDate);
}
```

```java

@Transactional
public void softDeleteCoupleTasks(List<Long> taskIds, Long coupleId) {
    List<CoupleTask> coupleTasksToDelete = coupleTaskRepository.findByTaskIdsAndCoupleId(
        taskIds, coupleId);

    if (coupleTasksToDelete.size() != taskIds.size()) {
        throw new RuntimeException("Some tasks not found or permission denied.");
    }

    coupleTasksToDelete.forEach(CoupleTask::updateDeleteStatus);
}
```

- Repository는 **순수 쿼리 책임**만 가지며, 비즈니스 조립은 Service에서 처리
- 쿼리 애너테이션 사용 , querydsl 사용
- **Fetch Join / @BatchSize** 병행으로 N+1 최소화
-
-

coupleTask 내

```java

@OneToMany(mappedBy = "coupleTask", cascade = CascadeType.ALL, orphanRemoval = true)
@BatchSize(size = 100) // 1:N 최적화
private List<SubTask> subTasks;
```

```java

@Query("select ct from CoupleTask ct " +
    "join fetch ct.task t " +
    "where ct.couple.id = :coupleId and ct.deleted = false")
List<CoupleTask> findByCoupleIdWithTask(@Param("coupleId") Long coupleId);

@Query("SELECT ct FROM CoupleTask ct "
    + "WHERE ct.task.id IN :taskIds AND "
    + "ct.couple.id = :coupleId AND "
    + "ct.deleted = false")
List<CoupleTask> findByTaskIdsAndCoupleId(@Param("taskIds") List<Long> taskIds,
    @Param("coupleId") Long coupleId);
```

```java

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
```

- 도메인 객체는 스스로 상태를 변경하고 관리합니다 (생성, 조회, 연관관계, 비즈니스 로직)

엔티티 생성 메서드

```java
  // private 생성자 (빌더 패턴용)
@Builder(access = AccessLevel.PRIVATE)
private User(String oauthId, String provider, String nickname, String profileImageUrl,
    String memo, String refreshToken, String email, Role role) {
    this.oauthId = oauthId;
    this.provider = provider;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.memo = memo;
    this.refreshToken = refreshToken;
    this.email = email;
    this.role = role;
}


// 정적 팩토리 메서드
public static User create(String oauthId, String provider) {
    return User.builder()
        .oauthId(oauthId)
        .provider(provider)
        .role(Role.USER) // 기본 역할 유저 -> 매칭안된상태
        .build();
}
```

엔티티 연관관계 편의 메서드

```java
    // 연관관계 편의 메서드
public void matchCouple(Couple couple) {
    this.couple = couple;
    if (couple != null) {
        couple.getUsers().add(this);
    }
}
```

단순 조회 및 변경 메서드

```java
    public void updateMemo(String memo) {
    if (memo != null && memo.length() > 18) {
        throw new IllegalArgumentException("메모는 18자를 초과할 수 없습니다.");
    }
    this.memo = memo;
}
```

비즈니스 메서드

```java
    // 비즈니스 메서드
public void initializeTasks(List<Task> tasks) {
    if (!coupleTasks.isEmpty()) {
        throw new IllegalStateException("Tasks already initialized for this couple");
    }

    tasks.forEach(task -> {
        CoupleTask coupleTask = CoupleTask.create(task, this);

        // 기본 SubTask 생성 책임을 CoupleTask로 위임
        coupleTask.initializeDefaultSubTasks();

        addCoupleTask(coupleTask);
    });
}
```

<br>
<hr>

## 🧪 테스트 전략

| 구분          | 목적             | 기술 스택                   |
|-------------|----------------|-------------------------|
| Controller  | API 응답 검증      | @WebMvcTest + MockMvc   |
| Service     | 비즈니스 로직 단위 테스트 | MockitoExtension        |
| Repository  | 쿼리 검증          | @DataJpaTest + QueryDSL |
| Domain      | 엔티티 상태 전이 검증   | JUnit5 + AssertJ        |
| Integration | 실제 시나리오 테스트    | @SpringBootTest         |

> ✅ 테스트의 질적 성장에 집중 — “좋은 테스트 vs 나쁜 테스트” 구분 실험  
> 실패 테스트도 개선의 일부로 기록


🧩 대표 테스트 코드

Domain Test

```java

@Test
@DisplayName("두 명의 User가 동일한 Couple에 매칭되어야 한다.")
void shouldMatchTwoUsersToSameCouple() {
    // Given
    User groom = User.create("groom_oauth", "kakao");
    User bride = User.create("bride_oauth", "naver");
    Couple couple = Couple.create();

    // When
    groom.matchCouple(couple);
    bride.matchCouple(couple);

    // Then
    assertThat(groom.getCouple()).isEqualTo(couple);
    assertThat(bride.getCouple()).isEqualTo(couple);
    assertThat(couple.getUsers())
        .containsExactlyInAnyOrder(groom, bride)
        .hasSize(2);
}
```

통합 테스트

```java

@Test
@DisplayName("메모 수정 통합 테스트 성공")
void updateMemo_integration_success() throws Exception {
    // given
    String newMemo = "새로운 메모입니다.";
    MemoRequestDto requestDto = new MemoRequestDto(newMemo);
    String requestBody = objectMapper.writeValueAsString(requestDto);

    // when
    mockMvc.perform(patch("/api/user/memo")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));

    // then
    // 영속성 컨텍스트의 변경 내용을 DB에 강제 반영하고, 컨텍스트를 비워 DB에서 새로 조회하도록 함
    entityManager.flush();
    entityManager.clear();

    User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
    assertEquals(newMemo, updatedUser.getMemo());
}
```

컨트롤러 슬라이스 테스ㅡㅌ

```java

@Test
@DisplayName("메모 수정 성공")
void updateMemo_success() throws Exception {
    // given
    Long loginUserId = 1L;
    String newMemoContent = "새로운 메모 내용";
    MemoRequestDto requestDto = new MemoRequestDto(newMemoContent);

    doNothing().when(userService).updateMemo(anyLong(), any(MemoRequestDto.class));

    // when & then
    mockMvc.perform(patch("/api/user/memo")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));

    // verify
    verify(userService).updateMemo(anyLong(), any(MemoRequestDto.class));
}
```

Repository Test

```java

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

```

Service Test

```java

@Test
@DisplayName("saveOrGetUser: 사용자가 존재하면 기존 사용자를 반환하고 저장하지 않는다.")
void saveOrGetUser_UserExists_ReturnsExistingUserAndDoesNotSave() {
    // Given
    String oauthId = "existing-oauth-id";
    String provider = "google";
    String profileImageUrl = "http://existing.profile.image/url.jpg";

    User existingUser = User.create(oauthId, provider);
    existingUser.updateProfileImage(profileImageUrl);

    // Mocking: userRepository.findByOauthId(oauthId)가 기존 User 객체를 반환하도록 설정
    when(userRepository.findByOauthId(oauthId)).thenReturn(Optional.of(existingUser));

    // When
    User resultUser = userService.saveOrGetUser(oauthId, provider, profileImageUrl);

    // Then
    assertThat(resultUser).isNotNull();
    assertThat(resultUser.getOauthId()).isEqualTo(oauthId);
    assertThat(resultUser.getProvider()).isEqualTo(provider);
    assertThat(resultUser.getProfileImageUrl()).isEqualTo(profileImageUrl);
    verify(userRepository, times(1)).findByOauthId(oauthId); // findByOauthId가 1번 호출되었는지 검증
    verify(userRepository, never()).save(any(User.class)); // save가 호출되지 않았는지 검증
}
```

---



커플코드 기반 매칭 기능: 초대 코드 입력 시 양방향 관계 생성

- `Couple` 엔티티 중심으로 `Member` 양방향 연결
- 예외처리: 이미 매칭된 사용자 / 잘못된 코드
- 마이페이지 기본 기능 구현

# 프로젝트 이미지

<br>

<img width="" height="515" alt="스크린샷 2025-09-12 오전 7 52 49" src="https://github.com/user-attachments/assets/9048e991-6694-4680-bfd7-5c0e1690b1c2" />


<img width="" height="256" alt="스크린샷 2025-09-12 오전 7 54 25" src="https://github.com/user-attachments/assets/b4863a0c-11d5-4cf0-bebe-99d273073f9c" />

<img width="" height="344" alt="스크린샷 2025-09-12 오전 7 55 30" src="https://github.com/user-attachments/assets/94896167-a98e-47ef-8f4b-b73ca6ce79a4" />



<img width="" height="364" alt="스크린샷 2025-09-12 오전 7 56 35" src="https://github.com/user-attachments/assets/8322c723-e1e2-4d3d-b9f6-b9a5be9d6801" />




<img width="" height="292" alt="스크린샷 2025-09-12 오전 7 58 27" src="https://github.com/user-attachments/assets/5a0e7c49-a73c-4baf-9830-a48b81ce835b" />

# 팀원

| <img src="https://github.com/user-attachments/assets/984d3041-b787-4da3-b07e-f2132411193e" width="150"> | <img src="https://github.com/user-attachments/assets/caf98b12-21c5-4396-80b5-d3054a36d33b" width="150"> |
|:-------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------:|
|                               [HyungGeun](https://github.com/HyungGeun94)                               |                                  [JeHyuck](https://github.com/jehyuck)                                  |
|                                                   BE                                                    |                                                   BE                                                    |

# 기술 스택

	• 프로그래밍 언어 및 프레임워크
      Java 17, SpringBoot

	• 데이터베이스 
      mysql 8.0

	• 도커 및 컨테이너화
      Docker, Docker Compose

    • 인프라 및 클라우드 관련 서비스
      aws ec2, route53, ELB, ECR, IAM, S3

    • DevOps 및 CI/CD 관련 도구
       github actions, postman, swagger

    • 협업 및 개발 도구 
       github, notion, discord, intelliJ

# ERD / 구조도 (작성예정)

# 주요 기능

```
회원가입 및 커플코드 매칭
```

```
함께 할 태스크 등록
```

# 주요 기능 일부 상세 코드

( 작성 예정 )

# 💭회고 (후기)

OAuth2 기반 인증과 커플 매칭 로직을 중심으로 서비스의 핵심 흐름을 구현했습니다.
<br>
<br>
도메인 중심 설계를 적용하며, 협업 과정에서 백엔드 간의 구조적 통일성과 설계 방향에 대해 논의할 수 있던 프로젝트입니다.
<br>
<br>
비록 프로토타입 단계에서 마무리되었지만, 추후 확장을 고려한 기반 설계를 목표로 진행되었습니다.


<p align="center"><i>© 2025 Team Wedy</i></p>

