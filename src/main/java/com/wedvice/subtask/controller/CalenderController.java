package com.wedvice.subtask.controller;

import com.wedvice.common.ApiResponse;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.subtask.dto.CalenderSummaryResponseDto;
import com.wedvice.subtask.service.CalenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calender")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "CalenderController API", description = "캘린더 페이지 관련 API (날짜 기반 조회)")
public class CalenderController {

    private final CalenderService calenderService;

    @Operation(
        summary = "캘린더 서브테스크 요약 조회",
        description = "연도와 월을 기준으로 커플의 서브테스크 요약 정보를 반환합니다."
    )
    @GetMapping("/summary/{year}/{month}")
    public ResponseEntity<ApiResponse<List<CalenderSummaryResponseDto>>> getCalenderSummary(
        @LoginUser CustomUserDetails loginUser,

        @PathVariable
        @Parameter(name = "year", description = "연도 (예: 2024)", example = "2024", required = true)
        @Min(2000) @Max(2100)
        int year,

        @PathVariable
        @Parameter(name = "month", description = "월 (1~12)", example = "7", required = true)
        @Min(1) @Max(12)
        int month
    ) {
        List<CalenderSummaryResponseDto> dto = calenderService.makeSummary(loginUser.getUserId(),
            year, month);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
