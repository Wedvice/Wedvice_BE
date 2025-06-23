package com.wedvice.coupletask.repository;

import com.wedvice.couple.entity.Couple;
import com.wedvice.coupletask.entity.CoupleTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoupleTaskRepository extends JpaRepository<CoupleTask, Long> {

    boolean findByCouple(Couple couple);

}
