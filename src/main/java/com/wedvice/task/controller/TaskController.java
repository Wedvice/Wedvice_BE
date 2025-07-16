package com.wedvice.task.controller;

import com.wedvice.common.ApiResponse;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.task.dto.DeleteTasksRequestDto;
import com.wedvice.task.dto.TaskResponseDTO;
import com.wedvice.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
@Tag(
    name = "coupleTask API",
    description = "👤 초기 task 탭 진입 시  API (상위 task 조회 및 삭제)"
)
public class TaskController {


    private final TaskService taskService;

    @Operation(
        summary = "커플 태스크 및 서브 태스크 조회",
        description = "커플 매칭된 사용자가 요청 시 삭제되지 않은 coupleTask 및 subTask 목록을 반환합니다.",
        security = @SecurityRequirement(name = "JWT"),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskResponseDTO.class)),
                    examples = @ExampleObject(
                        name = "성공 응답 예시",
                        value = """
                            {
                              "code": 200,
                              "message": "SUCCESS",
                              "data": [
                                {
                                  "taskId": 1,
                                  "taskTitle": "결혼식장 예약",
                                  "totalCount": 5,
                                  "completedCount": 2
                                },
                                {
                                  "taskId": 2,
                                  "taskTitle": "스튜디오 촬영",
                                  "totalCount": 3,
                                  "completedCount": 0
                                }
                              ]
                            }
                            """
                    )
                )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(
                        value = "{\"code\":401,\"message\":\"인증 실패\",\"data\":null}"
                    )
                )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(
                        value = "{\"code\":403,\"message\":\"권한 없음\",\"data\":null}"
                    )
                ))
        }
    )
    @GetMapping()
    public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getAllTaskAndSubTask(
        @LoginUser CustomUserDetails loginUser) {

        return ResponseEntity.ok(ApiResponse.success(taskService.findAllTaskAndSubTask(loginUser)));
    }

    @Operation(
        summary = "coupleTask 다중 삭제 요청",
        description = "커플 매칭된 사용자 요청 시 여러 coupleTask를 삭제 처리합니다.",
        security = @SecurityRequirement(name = "JWT"),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "삭제할 coupleTask ID 목록",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DeleteTasksRequestDto.class),
                examples = @ExampleObject(
                    name = "삭제 요청 예시",
                    value = "{\"taskIds\": [1, 2, 3]}"
                )
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class, example = "{\"code\":200,\"message\":\"SUCCESS\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 유효하지 않은 ID)",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(
                        value = "{\"code\":400,\"message\":\"잘못된 요청\",\"data\":null}"
                    )
                )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(
                        value = "{\"code\":401,\"message\":\"인증 실패\",\"data\":null}"
                    )
                )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(
                        value = "{\"code\":403,\"message\":\"권한 없음\",\"data\":null}"
                    )
                ))
        }
    )

    @DeleteMapping()
    public ResponseEntity<ApiResponse<Void>> deleteTasks(
        @LoginUser CustomUserDetails loginUser,
        @RequestBody DeleteTasksRequestDto requestDto) {

        taskService.deleteTasks(requestDto.getTaskIds(), loginUser);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


}
