package com.wedvice.subtask.repository;

import com.wedvice.subtask.dto.SubTaskResponseDTO;

import java.util.List;

public interface SubTaskCustomRepository {

    public List<SubTaskResponseDTO> getSubTasks(Long userId, Long taskId);

    }
