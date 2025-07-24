package com.wedvice.coupletask.service;

import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.coupletask.dto.CoupleTaskResponseDto;
import com.wedvice.coupletask.dto.CoupleTaskWithSubTaskInfo;
import com.wedvice.coupletask.repository.CoupleTaskRepository;
import com.wedvice.subtask.entity.SubTask;
import java.util.List;
import java.util.stream.Collectors;
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
    private final CoupleRepository coupleRepository;

    public boolean isCoupleHavingTasks(Couple couple) {
        return coupleTaskRepository.findByCouple(couple);
    }

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
        List<CoupleTask> coupleTasksToDelete = coupleTaskRepository.findByTaskIdsAndCoupleId(taskIds, coupleId);

        if (coupleTasksToDelete.size() != taskIds.size()) {
            throw new RuntimeException("Some tasks not found or permission denied.");
        }

        coupleTasksToDelete.forEach(CoupleTask::updateDeleteStatus);
    }

    @Transactional(readOnly = true)
    public List<CoupleTask> findByCoupleIdWithTask(final Long coupleId) {
        return coupleTaskRepository.findByCoupleIdWithTask(coupleId);
    }

    @Transactional(readOnly = true)
    public List<CoupleTaskResponseDto> getCoupleTasks(Long userId) {
        Couple couple = coupleRepository.findCoupleByUserId(userId);
        return coupleTaskRepository.findCoupleTaskWithTaskByCoupleId(couple.getId())
            .stream().map(CoupleTaskResponseDto::from)
            .toList();
    }
}
