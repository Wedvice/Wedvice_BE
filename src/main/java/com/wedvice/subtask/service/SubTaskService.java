package com.wedvice.subtask.service;

import com.wedvice.coupletask.repository.CoupleTaskRepository;
import com.wedvice.subtask.dto.CompleteRateResponseDto;
import com.wedvice.subtask.dto.SubTaskHomeResponseDto;
import com.wedvice.subtask.dto.SubTaskResponseDTO;
import com.wedvice.subtask.exception.NotExistRoleException;
import com.wedvice.subtask.repository.SubTaskRepository;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubTaskService {

  private final SubTaskRepository subTaskRepository;
  private final UserRepository userRepository;
  private final CoupleTaskRepository coupleTaskRepository;


  public List<SubTaskResponseDTO> getAllSubTask(Long userId, Long taskId) {

    User user = userRepository.findById(userId).orElseThrow();

    Long coupleId = user.getCouple().getId();

    if (coupleId == null) return List.of();

    return subTaskRepository.getSubTasks(userId, taskId, coupleId).stream()
        .map(subTask ->
            new SubTaskResponseDTO(
        subTask.getId(),subTask.getDisplayName(),subTask.getCompleted(),subTask.getRole().toString(),subTask.getPrice(),subTask.getTargetDate(),subTask.getContent(),subTask.getOrders()))
        .collect(Collectors.toList());
  }


  @Transactional(readOnly = true)
  public Slice<SubTaskHomeResponseDto> getHomeSubTasks(Long userId, Boolean completed, String role,
      Pageable pageable, boolean top3, String sort) {
    User.Role roleEnum = convertToRole(role); // 👈 문자열 → enum

    return subTaskRepository.findHomeSubTasksByCondition(userId, completed, top3, roleEnum, sort,
        pageable);
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

  public CompleteRateResponseDto getProgressRate(Long userId) {
    return subTaskRepository.getProgressRate(userId);
  }
}
