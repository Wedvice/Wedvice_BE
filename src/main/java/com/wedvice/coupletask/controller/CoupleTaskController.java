package com.wedvice.coupletask.controller;

import com.wedvice.common.ApiResponse;
import com.wedvice.coupletask.dto.CoupleTaskResponseDto;
import com.wedvice.coupletask.service.CoupleTaskService;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupletask")
@RequiredArgsConstructor
@Tag(name = "CoupleTask  API", description = "💑 리스트 관련 API (리스트 조회 등)")
@SecurityRequirement(name = "JWT")
@Slf4j
public class CoupleTaskController {

    private final CoupleTaskService coupleTaskService;

    @GetMapping
    @Operation(summary = "커플 테스크 리스트 조회",
        description = "커플이 삭제하지 않은 리스트와 리스트의 이름, 커플테스크 id를 조회합니다."
    )
    public ResponseEntity<ApiResponse<?>> getCoupleTaskList(
        @LoginUser CustomUserDetails loginUser) {
        List<CoupleTaskResponseDto> responseDto = coupleTaskService.getCoupleTasks(
            loginUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }
}
