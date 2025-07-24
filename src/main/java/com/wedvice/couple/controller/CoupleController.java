package com.wedvice.couple.controller;

import com.wedvice.common.ApiResponse;
import com.wedvice.couple.dto.CompleteMatchRequestDto;
import com.wedvice.couple.dto.CoupleHomeInfoResponseDto;
import com.wedvice.couple.dto.MatchCodeResponseDto;
import com.wedvice.couple.dto.MatchRequestDto;
import com.wedvice.couple.dto.UpdateWeddingDateRequestDto;
import com.wedvice.couple.service.CoupleService;
import com.wedvice.couple.util.MatchCodeService;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/couple")
@RequiredArgsConstructor
@Tag(name = "Couple API", description = "💑 커플 관련 API (결혼 날짜 업데이트 등)")
@SecurityRequirement(name = "JWT")
@Slf4j
public class CoupleController {

    private final CoupleService coupleService;
    private final MatchCodeService matchCodeService;

    //    api response 통일 안된거 체크.
    @GetMapping("/match-code")
    @Operation(summary = "매치코드 발급",
        description = "해당 유저의 매치코드가 존재하면 기존 코드를 반환하고, 없으면 새로 생성하여 반환합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "매치코드 발급 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MatchCodeResponseDto.class)
                )
            )
        })
    public ResponseEntity<ApiResponse<MatchCodeResponseDto>> getMatchCode(
        @LoginUser CustomUserDetails loginUser) {
        var responseDto = MatchCodeResponseDto.builder()
            .matchCode(matchCodeService.generateCode(loginUser.getUserId())).build();
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @PostMapping("/match")
    public ResponseEntity<ApiResponse<?>> match(@Validated @RequestBody MatchRequestDto request,
        @LoginUser CustomUserDetails loginUser) {
        long userId = loginUser.getUserId();
        coupleService.matchCouple(userId, request.getMatchCode());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping
    @Operation(
        summary = "커플 매칭 마지막 단계",
        description = "커플의 닉네임과 성별을 저장합니다."
    )

//    화면을 닉네임,성별 따로 할건지 아니면 합쳐서 보내줄건지 얘기 필요.
    public ResponseEntity<ApiResponse<?>> completeMatch(
        @Valid @RequestBody CompleteMatchRequestDto requestDto,
        @LoginUser CustomUserDetails loginUser) {
        coupleService.completeMatch(loginUser.getUserId(), requestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/summary")
    @Operation(
        summary = "홈페이지 커플 정보 조회",
        description = "커플의 웨딩 날짜, 메모, imageUrl을 조회합니다."
    )
    public ResponseEntity<ApiResponse<CoupleHomeInfoResponseDto>> getCoupleInfo(
        @LoginUser CustomUserDetails loginUser) {
        CoupleHomeInfoResponseDto coupleHomeInfoResponseDto = coupleService.getCoupleInfo(
            loginUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(coupleHomeInfoResponseDto));
    }

    @PatchMapping("/weddingDate")
    @Operation(
        summary = "결혼 예정일 수정",
        description = "커플의 결혼 예정일을 수정합니다."
    )
    public ResponseEntity<ApiResponse<Void>> updateWeddingDate(
        @LoginUser CustomUserDetails loginUser,
        @RequestBody @Valid UpdateWeddingDateRequestDto requestDto) {
        coupleService.updateWeddingDate(loginUser.getUserId(), requestDto.getWeddingDate());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
