package com.wedvice.task.controller;

import com.wedvice.common.ApiResponse;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.task.dto.TaskResponseDTO;
import com.wedvice.task.dto.DeleteTasksRequestDto;
import com.wedvice.task.service.TaskService;
import com.wedvice.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
@Tag(name = "coupleTask API", description = "👤 초기 task 탭 진입 시  API (task 조회 및 삭제)")
public class TaskController {


    private final TaskService taskService;

    @GetMapping()
    @Operation(summary = " 초기 task 탭 진입 시  task,subtask 조회 ", description = "커플 매칭된 사용자 요청 시 삭제되지 않은 coupleTask 및 subTask 보여줌")
    public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getAllTaskAndSubTask(@LoginUser CustomUserDetails loginUser){

        return ResponseEntity.ok(ApiResponse.success(taskService.findAllTaskAndSubTask(loginUser)));
    }

    @DeleteMapping
    @Operation(summary = "coupleTask 다중 삭제 요청", description = "커플 매칭된 사용자 요청 시 여러 coupleTask를 삭제 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteTasks(
            @LoginUser CustomUserDetails loginUser,
            @RequestBody DeleteTasksRequestDto requestDto) {

        taskService.deleteTasks(requestDto.getTaskIds(), loginUser);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


}
