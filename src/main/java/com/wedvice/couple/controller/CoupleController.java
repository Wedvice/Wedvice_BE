package com.wedvice.couple.controller;

import com.wedvice.couple.dto.CompleteMatchRequestDto;
import com.wedvice.couple.dto.MatchRequestDto;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.couple.service.CoupleService;
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


    @PostMapping("/match")
    public ResponseEntity<?> match(@RequestBody MatchRequestDto request, @LoginUser CustomUserDetails loginUser) {
        long userId = loginUser.getUserId();
        coupleService.matchCouple(userId, request.getMatchCode());
        return ResponseEntity.ok("성공");
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
