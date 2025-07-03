package com.wedvice.coupletask.service;

import com.wedvice.couple.entity.Couple;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.coupletask.repository.CoupleTaskRepository;
import com.wedvice.subtask.service.SubTaskService;
import com.wedvice.task.entity.Task;
import com.wedvice.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CoupleTaskService {


    private final CoupleTaskRepository coupleTaskRepository;
    private final TaskService taskService;
    private final SubTaskService subTaskService;

    public boolean isCoupleHavingTasks(Couple couple) {
        return coupleTaskRepository.findByCouple(couple);
    }
}
