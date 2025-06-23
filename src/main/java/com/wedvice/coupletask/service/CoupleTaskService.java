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

//커플 매칭이 완료 됐다고 판단되는순간 최초1회만 coupleTask가 만들어져야함.

    private final CoupleTaskRepository coupleTaskRepository;
    private final TaskService taskService;
    private final SubTaskService subTaskService;


    public void createCoupleWithTasks(Couple couple) {

        List<Task> tasks = taskService.findAllTask();

        List<CoupleTask> coupleTasks = tasks.stream()
                .map(task -> CoupleTask.builder()
                        .couple(couple)
                        .task(task)
                        .deleted(false)
                        .build())
                .collect(Collectors.toList());

        coupleTaskRepository.saveAll(coupleTasks);


        // 위에 이어서 추가
        coupleTasks.forEach(coupleTask -> {
            subTaskService.createDefaultSubTasksFor(coupleTask);
        });

    }

    public boolean isCoupleHavingTasks(Couple couple) {
        return coupleTaskRepository.findByCouple(couple);
    }
}
