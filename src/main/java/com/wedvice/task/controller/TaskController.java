package com.wedvice.task.controller;

import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.task.dto.TaskResponseDTO;
import com.wedvice.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {


    private final TaskService taskService;

//    responseEntity로 바꾸기.. 응답포맷
    @GetMapping()
    public List<TaskResponseDTO> getAllTaskAndSubTask(@LoginUser CustomUserDetails loginUser){

        return taskService.findAllTaskAndSubTask(loginUser);
    }

    @DeleteMapping()
    public boolean deleteTask(@LoginUser CustomUserDetails loginUser, @RequestBody Long taskId){

        taskService.delete(taskId,loginUser);


        return true;
    }


}
