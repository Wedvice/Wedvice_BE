package com.wedvice.couple.repository;

import com.wedvice.couple.entity.Couple;
import com.wedvice.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CoupleRepository extends JpaRepository<Couple, Long> {

    // ✅ 특정 커플의 결혼 날짜 업데이트
    @Modifying
    @Query("UPDATE Couple c SET c.weddingDate = :weddingDate WHERE c.id = :coupleId")
    void updateWeddingDate(Long coupleId, java.time.LocalDate weddingDate);

    boolean existsByGroomAndBride(User groom, User bride);
}
