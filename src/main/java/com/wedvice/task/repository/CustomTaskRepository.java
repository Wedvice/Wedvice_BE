package com.wedvice.task.repository;

import com.wedvice.task.dto.TaskResponseDTO;

import java.util.List;

public interface CustomTaskRepository {

    List<TaskResponseDTO> getAllTaskAndSubTask(Long userId);



}
