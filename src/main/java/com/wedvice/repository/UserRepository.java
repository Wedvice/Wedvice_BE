package com.wedvice.repository;

import com.wedvice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ 카카오 OAuth ID와 Provider를 기준으로 사용자 찾기
    Optional<User> findByOauthIdAndProvider(String oauthId, String provider);

    // ✅ 닉네임 업데이트 (매칭된 사용자 ID 제외)
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.nickname = :nickname WHERE u.id = :userId")
    void updateNickname(Long userId, String nickname);

    // ✅ 매칭된 사용자 ID 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.matchedUserId = :matchedUserId, u.couple.id = :coupleId WHERE u.id = :userId")
    void updateMatchedUserId(@Param("userId") Long userId,
                             @Param("matchedUserId") Long matchedUserId,
                             @Param("coupleId") Long coupleId);

    @Modifying
    @Query("UPDATE User u SET u.refreshToken = :refreshToken WHERE u.id = :id")
    void updateRefreshToken(@Param("id") Long id, @Param("refreshToken") String refreshToken);
}

