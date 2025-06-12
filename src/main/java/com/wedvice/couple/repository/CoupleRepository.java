package com.wedvice.couple.repository;

import com.wedvice.couple.entity.Couple;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
}
