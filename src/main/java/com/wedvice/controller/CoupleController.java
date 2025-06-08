package com.wedvice.controller;

import com.wedvice.dto.CompleteMatchRequestDto;
import com.wedvice.dto.MatchRequestDto;
import com.wedvice.entity.Couple;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.service.CoupleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @PatchMapping("/update-wedding-date/{coupleId}")
    @Operation(
            summary = "💒 결혼 날짜 업데이트",
            description = "특정 커플의 결혼 날짜를 업데이트합니다. 날짜 형식은 YYYY-MM-DD이어야 합니다."
    )
    public ResponseEntity<String> updateWeddingDate(
            @Parameter(description = "결혼 날짜를 업데이트할 커플의 ID", example = "1")
            @PathVariable Long coupleId,

            @Parameter(description = "업데이트할 결혼 날짜 (형식: YYYY-MM-DD)", example = "2025-10-10")
            @RequestParam String weddingDate) {

        coupleService.updateWeddingDate(coupleId, weddingDate);
        return ResponseEntity.ok("결혼 날짜가 업데이트되었습니다.");
    }

    @PostMapping("/match")
    public ResponseEntity<?> math(@RequestBody MatchRequestDto request, @LoginUser CustomUserDetails loginUser) {
        long userId = loginUser.getUserId();
        coupleService.matchCouple(userId, request.getMatchCode());
        return ResponseEntity.ok("성공");
    }

    // 커플 정보 조회 API 추가
    @GetMapping("/{coupleId}")
    @Operation(
            summary = "👀 커플 정보 조회",
            description = "지정된 커플 ID의 정보를 조회합니다."
    )
    public ResponseEntity<Couple> getCouple(
            @Parameter(description = "조회할 커플의 ID", example = "1")
            @PathVariable Long coupleId) {

        Couple couple = coupleService.getCouple(coupleId);
        return ResponseEntity.ok(couple);
    }

    @PostMapping
    @Operation(
            summary = "커플 매칭 마지막 단계",
            description = "커플의 닉네임과 성별을 저장합니다."
    )
    public ResponseEntity<?> completeMatch(@RequestBody CompleteMatchRequestDto requestDto, @LoginUser CustomUserDetails loginUser) {
        coupleService.completeMatch(loginUser.getUserId(), requestDto);
        return ResponseEntity.ok().build();
    }
}
