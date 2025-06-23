package com.wedvice.task.service;

import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.task.dto.TaskResponseDTO;
import com.wedvice.task.entity.Task;
import com.wedvice.task.repository.TaskRepository;
import com.wedvice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;


    public List<Task> findAllTask(){
        return taskRepository.findAll();
    }

    public List<TaskResponseDTO> findAllTaskAndSubTask(CustomUserDetails customUserDetails) {

        Long userId = customUserDetails.getUserId();

        return taskRepository.getAllTaskAndSubTask(userId);


    }



}
