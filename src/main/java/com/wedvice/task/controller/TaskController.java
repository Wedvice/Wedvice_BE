package com.wedvice.task.controller;

import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.task.dto.TaskResponseDTO;
import com.wedvice.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {


    private final TaskService taskService;

    @GetMapping()
    public List<TaskResponseDTO> getAllTaskAndSubTask(@LoginUser CustomUserDetails loginUser){

        return taskService.findAllTaskAndSubTask(loginUser);
    }

    @DeleteMapping()
    public boolean deleteTask(){

        return true;
    }


}
