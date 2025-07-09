package com.wedvice.subtask.service;

import com.wedvice.subtask.dto.CompleteRateResponseDto;
import com.wedvice.subtask.dto.HomeSubTaskConditionDto;
import com.wedvice.subtask.dto.SubTaskHomeResponseDto;
import com.wedvice.subtask.dto.SubTaskResponseDTO;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.subtask.exception.NotExistRoleException;
import com.wedvice.subtask.repository.SubTaskRepository;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubTaskService {

    private final SubTaskRepository subTaskRepository;
    private final UserRepository userRepository;

  public List<SubTaskResponseDTO> getAllSubTask(Long userId, Long taskId) {

    return subTaskRepository.getSubTasks(userId, taskId);
  }


    @Transactional(readOnly = true)
    public Slice<SubTaskHomeResponseDto> getHomeSubTasks(Long userId, Boolean completed,
        String roleStr, Pageable pageable, boolean top3, String sort) {
        User.Role role = convertToRole(roleStr);

        Long coupleId = userRepository.findCoupleIdByUserId(userId);
        if (coupleId == null) {
            return new SliceImpl<>(List.of());
        }

        HomeSubTaskConditionDto homeSubTaskConditionDto = HomeSubTaskConditionDto.builder()
            .coupleId(coupleId)
            .completed(completed)
            .pageable(pageable)
            .role(role)
            .sort(sort)
            .top3(top3)
            .build();
        List<SubTask> tasks = subTaskRepository.findHomeSubTasksByCondition(
            homeSubTaskConditionDto);
        List<SubTaskHomeResponseDto> dtos = tasks.stream()
            .map(st -> SubTaskHomeResponseDto.builder()
                .subTaskId(st.getId())
                .coupleTaskId(st.getCoupleTask().getId())
                .subTaskContent(st.getContent())
                .taskContent(st.getCoupleTask().getTask().getTitle())
                .targetDate(st.getTargetDate())
                .completed(st.getCompleted())
                .orders(st.getOrders())
                .build())
            .toList();

        boolean hasNext = !top3 && dtos.size() > pageable.getPageSize();
        if (hasNext) {
            dtos.remove(dtos.size() - 1);
        }
        return new SliceImpl<>(dtos, pageable, hasNext);
    }

    private User.Role convertToRole(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            return null;
        }
        try {
            return User.Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotExistRoleException();
        }
    }

    @Transactional(readOnly = true)
    public CompleteRateResponseDto getProgressRate(Long userId, String role) {
        Long coupleId = userRepository.findCoupleIdByUserId(userId);

        if (coupleId == null) {
            return new CompleteRateResponseDto(0, 0, 0);
        }

        User.Role filterRole = convertToRole(role);
        return subTaskRepository.getProgressRate(userId, filterRole);
    }
}
