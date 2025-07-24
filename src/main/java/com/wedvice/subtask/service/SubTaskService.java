package com.wedvice.subtask.service;

import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.coupletask.repository.CoupleTaskRepository;
import com.wedvice.subtask.dto.CompleteRateResponseDto;
import com.wedvice.subtask.dto.CreateSubTaskRequestDTO;
import com.wedvice.subtask.dto.HomeSubTaskConditionDto;
import com.wedvice.subtask.dto.SubTaskHomeResponseDto;
import com.wedvice.subtask.dto.SubTaskResponseDTO;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.subtask.exception.NotExistRoleException;
import com.wedvice.subtask.repository.SubTaskRepository;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.access.AccessDeniedException;
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

        if (coupleId == null) {
            return List.of();
        }

        return subTaskRepository.getSubTasks(userId, taskId, coupleId).stream()
            .map(subTask ->
                new SubTaskResponseDTO(
                    subTask.getCoupleTask().getId(), subTask.getId(), subTask.getDisplayName(),
                    subTask.getCompleted(), subTask.getRole().toString(), subTask.getPrice(),
                    subTask.getTargetDate(), subTask.getCompletedDate(), subTask.getContent(),
                    subTask.getOrders()))
            .collect(Collectors.toList());
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


    // SubTask 삭제 (soft delete)
    public void deleteSubTask(Long userId, Long subTaskId) {
        SubTask subTask = subTaskRepository.findById(subTaskId)
            .orElseThrow(
                () -> new IllegalArgumentException("SubTask not found with id: " + subTaskId));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // SubTask의 CoupleTask와 User의 Couple이 동일한지 확인
        if (!subTask.getCoupleTask().getCouple().getId().equals(user.getCouple().getId())) {
            throw new AccessDeniedException("You do not have permission to delete this SubTask.");
        }

        subTask.updateDeleteStatus();
    }

    // SubTask 완료 상태 변경
    public void updateSubTaskCompletedStatus(Long userId, Long subTaskId) {
        SubTask subTask = subTaskRepository.findById(subTaskId)
            .orElseThrow(
                () -> new IllegalArgumentException("SubTask not found with id: " + subTaskId));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // SubTask의 CoupleTask와 User의 Couple이 동일한지 확인
        if (!subTask.getCoupleTask().getCouple().getId().equals(user.getCouple().getId())) {
            throw new AccessDeniedException(
                "You do not have permission to update this SubTask's completed status.");
        }

        subTask.updateCompleteStatus();
    }

    // SubTask 정렬 순서 변경
    @Transactional
    public void updateSubTaskOrders(Long userId, List<Long> subTaskIds) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Long userCoupleId = user.getCouple().getId();

        for (int i = 0; i < subTaskIds.size(); i++) {
            Long subTaskId = subTaskIds.get(i);
            SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(
                    () -> new IllegalArgumentException("SubTask not found with id: " + subTaskId));

            // 권한 확인: SubTask가 사용자의 커플에 속하는지 확인
            if (!subTask.getCoupleTask().getCouple().getId().equals(userCoupleId)) {
                throw new AccessDeniedException(
                    "You do not have permission to align this SubTask.");
            }

            // orders 값 업데이트
            subTask.updateOrders(i);
        }
    }

    // SubTask 생성
    public SubTask createSubTask(Long userId, CreateSubTaskRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        CoupleTask coupleTask = coupleTaskRepository.findById(requestDTO.getCoupleTaskId())
            .orElseThrow(() -> new IllegalArgumentException(
                "CoupleTask not found with id: " + requestDTO.getCoupleTaskId()));

        // 권한 확인: CoupleTask가 현재 로그인한 사용자의 커플에 속하는지 확인
        if (!coupleTask.getCouple().getId().equals(user.getCouple().getId())) {
            throw new AccessDeniedException(
                "You do not have permission to create a SubTask for this CoupleTask.");
        }

        // SubTask.create 팩토리 메서드를 사용하여 SubTask 엔티티 생성
        SubTask newSubTask = SubTask.create(
            coupleTask,
            requestDTO.getTitle(),
            0,
            requestDTO.getLocalDate(),
            requestDTO.getRole(),
            0,
            requestDTO.getDescription()
        );

        return subTaskRepository.save(newSubTask);
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
