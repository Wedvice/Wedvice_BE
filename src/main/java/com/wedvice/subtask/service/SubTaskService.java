package com.wedvice.subtask.service;

import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.subtask.dto.CompleteRateResponseDto;
import com.wedvice.subtask.dto.SubTaskHomeResponseDto;
import com.wedvice.subtask.dto.SubTaskResponseDTO;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.subtask.exception.NotExistRoleException;
import com.wedvice.subtask.repository.SubTaskRepository;
import com.wedvice.task.entity.Task;
import com.wedvice.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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


    @Transactional(readOnly = true)
    public Slice<SubTaskHomeResponseDto> getHomeSubTasks(Long userId, boolean completed, String role, Pageable pageable, boolean top3, String sort) {
        User.Role roleEnum = convertToRole(role); // 👈 문자열 → enum

        return subTaskRepository.findHomeSubTasksByCondition(userId, completed, top3, roleEnum, sort, pageable);
    }

    private User.Role convertToRole(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) return null;
        try {
            return User.Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotExistRoleException();
        }
    }

    public CompleteRateResponseDto getProgressRate(Long userId) {
        return subTaskRepository.getProgressRate(userId);
    }
}
