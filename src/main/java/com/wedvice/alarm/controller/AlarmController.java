package com.wedvice.alarm.controller;

import com.wedvice.alarm.dto.AlarmResponseDto;
import com.wedvice.alarm.service.AlarmService;
import com.wedvice.common.ApiResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alarm")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
@Tag(name = "Alarm API", description = " 알람 관련 API (알람 조회, 읽기 등)")
@Slf4j
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping
    @Operation(
        summary = "내 알림 목록 조회",
        description = "현재 로그인한 사용자의 모든 알림을 최신순으로 조회합니다."
    )
    public ResponseEntity<ApiResponse<List<AlarmResponseDto>>> getMyAlarms(@LoginUser
    CustomUserDetails loginUser) {
        return ResponseEntity.ok(
            ApiResponse.success(alarmService.getMyAlarms(loginUser.getUserId()))
        );
    }

    @PatchMapping("/{alarmId}/read")
    @Operation(
        summary = "알림 읽음 처리",
        description = "알림 ID를 기반으로 해당 알림을 읽음 처리합니다. 본인 알림이 아닌 경우 실패합니다."
    )
    public ResponseEntity<ApiResponse<Void>> readAlarm(@PathVariable Long alarmId,
        @LoginUser CustomUserDetails loginUser) {
        alarmService.readAlarm(alarmId, loginUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
