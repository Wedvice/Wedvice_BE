package com.wedvice.coupletask.repository;

import com.wedvice.couple.entity.Couple;
import com.wedvice.coupletask.entity.CoupleTask;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoupleTaskRepository extends JpaRepository<CoupleTask, Long>,
    CoupleTaskCustomRepository {

    boolean findByCouple(Couple couple);

    @Query("select ct from CoupleTask ct " +
        "join fetch ct.task t " +
        "where ct.couple.id = :coupleId and ct.deleted = false")
    List<CoupleTask> findByCoupleIdWithTask(@Param("coupleId") Long coupleId);


    Optional<CoupleTask> findByTaskIdAndCoupleId(Long taskId, Long coupleId);

}
