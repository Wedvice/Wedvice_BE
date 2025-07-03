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
