package com.wedvice.subtask.repository;

import com.wedvice.subtask.dto.CompleteRateResponseDto;
import com.wedvice.subtask.dto.SubTaskHomeResponseDto;
import com.wedvice.subtask.dto.SubTaskResponseDTO;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface SubTaskCustomRepository {

  List<SubTask> getSubTasks(Long userId, Long taskId, Long coupleId);

  Slice<SubTaskHomeResponseDto> findHomeSubTasksByCondition(Long userId,
      Boolean completed,
      boolean top3,
      User.Role role, String sortType,
      Pageable pageable);

  CompleteRateResponseDto getProgressRate(Long userId);
}
