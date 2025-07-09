package com.wedvice.subtask.repository;

import com.wedvice.subtask.dto.CompleteRateResponseDto;
import com.wedvice.subtask.dto.HomeSubTaskConditionDto;
import com.wedvice.subtask.dto.SubTaskResponseDTO;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.user.entity.User.Role;
import java.util.List;

public interface SubTaskCustomRepository {

    List<SubTaskResponseDTO> getSubTasks(Long userId, Long taskId);

    List<SubTask> findHomeSubTasksByCondition(HomeSubTaskConditionDto homeSubTaskConditionDto);

    CompleteRateResponseDto getProgressRate(Long userId, Role filterRole);
}
