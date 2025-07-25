package com.wedvice.controller.testuser;

import com.wedvice.couple.exception.InvalidUserAccessException;
import com.wedvice.couple.util.MatchCodeService;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestUserService {

    private final MatchCodeService matchCodeService;
    private final UserRepository userRepository;


    @Transactional
    public CreateTestUserResponse createTestUser(Long userId) {
        User testUser = User.createTestUser();
        userRepository.save(testUser);
        String code = matchCodeService.generateCode(testUser.getId());
        return CreateTestUserResponse.from(code, testUser.getId());
    }

    @Transactional
    public void updateDetail(Long testUserId, Long loginUser) {
        User testUser = userRepository.findByUserWithCoupleAndPartner(testUserId)
            .orElseThrow(InvalidUserAccessException::new);

        User user = testUser.getPartnerOrThrow();
        log.info("[testUserService#updateDetail] testUserId : {}, loginUserId : {}", testUserId,
            loginUser);
        if (!user.getId().equals(loginUser)) {
            throw new TestUserNotMatchedCreatedUserException();
        }

        testUser.updateNickname("테스");
        testUser.updateRole(
            user.getRole().equals(User.Role.BRIDE) ? User.Role.BRIDE : User.Role.GROOM);
    }

    @Scheduled(cron = "*/3 * * * * *")// 매일 새벽 3시
    @Transactional
    public void deleteUnusedTestUsers() {
        LocalDateTime cutoff = LocalDateTime.now();

        List<User> users = userRepository.findAllByIsTestTrueAndCoupleIsNullAndCreatedAtBefore(
            cutoff);
        log.info("[testUserService#deleteUnusedTestUsers] userSize : {}", users.size());
        for (User user : users) {
            log.info("[testUserService#deleteUnusedTestUsers] 테스트 유저 삭제 role = {}, id = {}",
                user.getRole(), user.getId());
            userRepository.delete(user); // 연관 엔티티 주의
        }
    }
}
