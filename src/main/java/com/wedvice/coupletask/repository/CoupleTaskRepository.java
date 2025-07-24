package com.wedvice.coupletask.repository;

import com.wedvice.coupletask.entity.CoupleTask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoupleTaskRepository extends JpaRepository<CoupleTask, Long>,
    CoupleTaskCustomRepository {

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
}
