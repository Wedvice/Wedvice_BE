package com.wedvice.coupletask.repository;

import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.subtask.entity.SubTask;
import java.util.List;

public interface CoupleTaskCustomRepository {

    List<SubTask> findSubTasksForCoupleTaskInfo(Long coupleId);


    List<CoupleTask> findCoupleTaskWithTaskByCoupleId(Long id);
}
