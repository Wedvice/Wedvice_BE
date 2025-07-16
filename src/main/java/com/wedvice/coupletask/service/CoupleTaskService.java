package com.wedvice.coupletask.service;

import com.wedvice.coupletask.dto.CoupleTaskWithSubTaskInfo;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.coupletask.repository.CoupleTaskRepository;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.subtask.service.SubTaskService;
import com.wedvice.task.service.TaskService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CoupleTaskService {


    private final CoupleTaskRepository coupleTaskRepository;
    private final TaskService taskService;
    private final SubTaskService subTaskService;

    @Transactional(readOnly = true)
    public List<CoupleTaskWithSubTaskInfo> getCoupleTasksWithSubTaskInfo(Long coupleId) {
        List<SubTask> subTasks = coupleTaskRepository.findSubTasksForCoupleTaskInfo(coupleId);
        return subTasks.stream()
            .map(subTask -> new CoupleTaskWithSubTaskInfo(
                subTask.getCoupleTask().getId(),
                subTask.getCoupleTask().getTask().getTitle(),
                subTask.getPrice()
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public void softDeleteCoupleTasks(List<Long> taskIds, Long coupleId) {
        taskIds.forEach(taskId -> {
            CoupleTask coupleTask = coupleTaskRepository.findByTaskIdAndCoupleId(taskId, coupleId)
                .orElseThrow(() -> new RuntimeException(
                    "CoupleTask not found or permission denied for taskId: " + taskId));
            coupleTask.updateDeleteStatus();
        });
    }
}
