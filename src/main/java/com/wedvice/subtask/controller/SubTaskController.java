package com.wedvice.subtask.controller;

import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.subtask.dto.SubTaskResponseDTO;
import com.wedvice.subtask.service.SubTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/subtask")
public class SubTaskController {

    private final SubTaskService subTaskService;

    @GetMapping
    public List<SubTaskResponseDTO> get(@LoginUser CustomUserDetails loginUser, long taskId){

        return subTaskService.getAllSubTask(loginUser.getUserId(),taskId);

    }

    @PatchMapping("/align")
    public String patchAlign(){
        return "subtask 정렬 위치 변경";
    }


    @PostMapping()
    public String post(){
        return "create subtask";
    }

    @DeleteMapping()
    public String delete(){
        return "delete subtask";
    }


}
