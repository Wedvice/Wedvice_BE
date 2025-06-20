package com.wedvice.couple.controller;

import com.wedvice.common.ApiResponse;
import com.wedvice.couple.dto.CompleteMatchRequestDto;
import com.wedvice.couple.dto.CoupleHomeInfoResponseDto;
import com.wedvice.couple.dto.MatchCodeResponseDto;
import com.wedvice.couple.dto.MatchRequestDto;
import com.wedvice.couple.service.CoupleService;
import com.wedvice.couple.util.MatchCodeService;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/couple")
@RequiredArgsConstructor
@Tag(name = "Couple API", description = "💑 커플 관련 API (결혼 날짜 업데이트 등)")
public class CoupleController {
    private final CoupleService coupleService;
    private final MatchCodeService matchCodeService;

    @PostMapping("/match")
    public ResponseEntity<ApiResponse<?>> match(@RequestBody MatchRequestDto request, @LoginUser CustomUserDetails loginUser) {
        long userId = loginUser.getUserId();
        coupleService.matchCouple(userId, request.getMatchCode());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping
    @Operation(
            summary = "커플 매칭 마지막 단계",
            description = "커플의 닉네임과 성별을 저장합니다."
    )
    public ResponseEntity<ApiResponse<?>> completeMatch(@RequestBody CompleteMatchRequestDto requestDto, @LoginUser CustomUserDetails loginUser) {
        coupleService.completeMatch(loginUser.getUserId(), requestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/summary")
    @Operation(
            summary = "홈페이지 커플 정보 조회",
            description = "커플의 웨딩 날짜, 메모, imageUrl을 조회합니다."
    )
    public ResponseEntity<ApiResponse<CoupleHomeInfoResponseDto>> getCoupleInfo(@LoginUser CustomUserDetails loginUser) {
        CoupleHomeInfoResponseDto coupleHomeInfoResponseDto = coupleService.getCoupleInfo(loginUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(coupleHomeInfoResponseDto));
    }

    @GetMapping("/match-code")
    public ResponseEntity<MatchCodeResponseDto> getMatchCode(@LoginUser CustomUserDetails loginUser) {
        var responseDto = MatchCodeResponseDto.builder().matchCode(matchCodeService.generateCode(loginUser.getUserId())).build();
        return ResponseEntity.ok(responseDto);
    }

}
