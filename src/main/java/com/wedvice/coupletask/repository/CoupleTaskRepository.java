package com.wedvice.coupletask.repository;

import com.wedvice.couple.entity.Couple;
import com.wedvice.coupletask.entity.CoupleTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoupleTaskRepository extends JpaRepository<CoupleTask, Long> {

    boolean findByCouple(Couple couple);

    @Query("select ct from CoupleTask ct " +
            "join fetch ct.task t " +
            "where ct.couple.id = :coupleId and ct.deleted = false")
    public List<CoupleTask> findByCoupleIdWithTask(@Param("coupleId") Long coupleId);


    public Optional<CoupleTask> findByTaskIdAndCoupleId(Long taskId,Long  coupleId);

}
