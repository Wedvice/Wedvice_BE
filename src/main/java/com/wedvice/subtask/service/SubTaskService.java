package com.wedvice.subtask.service;

import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.subtask.dto.SubTaskResponseDTO;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.subtask.repository.SubTaskRepository;
import com.wedvice.task.entity.Task;
import com.wedvice.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubTaskService {

    private final SubTaskRepository subTaskRepository;

    public void createDefaultSubTasksFor(CoupleTask coupleTask) {
        Task task = coupleTask.getTask();

        // SubTask 템플릿을 불러오거나 직접 정의
        List<SubTask> subTasks = List.of(
                SubTask.builder()
                        .coupleTask(coupleTask)
                        .orders(0)
                        .displayName("기본 서브태스크 1")
                        .role(User.Role.GROOM)
                        .price(0)
                        .contents("설명1")
                        .targetDate(LocalDate.now().plusDays(3))
                        .completed(false)
                        .build(),
                SubTask.builder()
                        .coupleTask(coupleTask)
                        .orders(1)
                        .displayName("기본 서브태스크 2")
                        .role(User.Role.BRIDE)
                        .price(100000)
                        .contents("설명2")
                        .targetDate(LocalDate.now().plusDays(5))
                        .completed(false)
                        .build()
        );

        subTaskRepository.saveAll(subTasks);
    }

    public List<SubTaskResponseDTO> getAllSubTask(Long userId, Long taskId) {

        return subTaskRepository.getSubTasks(userId, taskId);
    }


}
