package com.wedvice.subtask.controller;

import com.wedvice.common.ApiResponse;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.subtask.dto.CompleteRateResponseDto;
import com.wedvice.subtask.dto.CreateSubTaskRequestDTO;
import com.wedvice.subtask.dto.SubTaskHomeResponseDto;
import com.wedvice.subtask.dto.SubTaskResponseDTO;
import com.wedvice.subtask.service.SubTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/subtask")
@SecurityRequirement(name = "JWT")
public class SubTaskController {

  private final SubTaskService subTaskService;

  @GetMapping
  public List<SubTaskResponseDTO> get(@LoginUser CustomUserDetails loginUser, long taskId) {

    return subTaskService.getAllSubTask(loginUser.getUserId(), taskId);

  }

  @PatchMapping("/align")
  public String patchAlign() {
    return "subtask 정렬 위치 변경";
  }



    @PostMapping()
    public String post(@RequestBody CreateSubTaskRequestDTO createSubTaskRequestDTO) {

        log.info("========== "+createSubTaskRequestDTO);


        return "create subtask success";
    }


    @DeleteMapping()
    public String delete(@RequestBody Long subTaskId) {
        return "delete subtask";
    }

    @PatchMapping
    public String subTaskCompleted(){

        return "subtask status change completed";

    }



  @GetMapping("/home")
  @Operation(summary = "서브태스크 홈 목록 조회", description = "완료 여부 및 top3 여부에 따라 목록을 조회합니다. 무한스크롤 지원")
  public ResponseEntity<ApiResponse<Slice<SubTaskHomeResponseDto>>> getHomeSubtask(
      @LoginUser CustomUserDetails loginUser,

      @Parameter(description = "완료 여부 (true = 완료, false = 남은, null = 전부 조회)", required = false)
      @RequestParam(required = false) Boolean completed,

      @Parameter(description = "top3 여부 (true면 상위 3개만 반환)", required = false)
      @RequestParam(defaultValue = "false") boolean top3,

      @Parameter(description = "정렬 기준 설정", required = false)
      @RequestParam(defaultValue = "date") String sort,

      @Parameter(description = "역할 (TOGETHER = 함께, BRIDE = 신부, GROOM = 신부, null = 전부 조회)", required = false)
      @RequestParam(required = false) String role,

      @ParameterObject // Swagger에서 page, size, sort
      @PageableDefault(size = 10) Pageable pageable
  ) {
    Slice<SubTaskHomeResponseDto> responseDto = subTaskService.getHomeSubTasks(
        loginUser.getUserId(), completed, role, pageable, top3, sort);
    return ResponseEntity.ok(ApiResponse.success(responseDto));
  }

    @GetMapping("/progress")
    @Operation(summary = "완료율 조회", description = "커플의 전체 SubTask 완료율을 조회합니다.")
    public ResponseEntity<ApiResponse<CompleteRateResponseDto>> getProgress(
        @LoginUser CustomUserDetails loginUser,

        @Parameter(
            name = "role",
            description = "조회할 역할 (GROOM: 신랑, BRIDE: 신부, TOGETHER: 함께). 미지정 시 전체 기준으로 계산됩니다.",
            required = false,
            example = "GROOM"
        )
        @RequestParam(name = "role", required = false) String role) {
        CompleteRateResponseDto responseDto = subTaskService.getProgressRate(loginUser.getUserId(),
            role);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }
}
