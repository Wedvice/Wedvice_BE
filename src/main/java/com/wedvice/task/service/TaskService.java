package com.wedvice.task.service;

import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.coupletask.service.CoupleTaskService;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.task.dto.TaskResponseDTO;
import com.wedvice.task.entity.Task;
import com.wedvice.task.repository.TaskRepository;
import com.wedvice.user.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final CoupleTaskService coupleTaskService;


    public List<Task> findAllTask() {
        return taskRepository.findAll();
    }

    public List<TaskResponseDTO> findAllCoupleTaskAndSubTask(CustomUserDetails loginUser) {

        Long coupleId = userService.getCoupleIdForUser(loginUser.getUserId());

        List<CoupleTask> coupleTasks = coupleTaskService.findByCoupleIdWithTask(coupleId);

        return coupleTasks.stream()
            .map(ct -> new TaskResponseDTO(
                ct.getTask().getId(),
                ct.getTask().getTitle(),
                (long) ct.getSubTasks().size(),
                (int) ct.getSubTasks().stream().filter(SubTask::getCompleted).count()
            ))
            .collect(Collectors.toList());
    }

    public void deleteTasks(List<Long> taskIds, CustomUserDetails loginUser) {

        Long coupleId = userService.getCoupleIdForUser(loginUser.getUserId());
        coupleTaskService.softDeleteCoupleTasks(taskIds, coupleId);
    }
}
