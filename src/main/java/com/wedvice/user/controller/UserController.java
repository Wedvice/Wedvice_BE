package com.wedvice.user.controller;

import static org.springframework.security.config.Elements.JWT;

import com.wedvice.common.ApiResponse;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.user.dto.MemoRequestDto;
import com.wedvice.user.dto.MyPageMainResponseDto;
import com.wedvice.user.dto.UserDto;
import com.wedvice.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "👤 사용자 관련 API (메모 업데이트, 매칭, 사용자 정보 업데이트 등)")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @Operation(
        summary = "📝 간단한 유저 정보 불러오기(테스트)",
        description = "사용자 id 입력 시, 간단한 정보 가져옴",
        parameters = {
            @Parameter(
                name = "userId",
                description = "정보 조회할 userId",
                required = true,
                in = ParameterIn.PATH // 쿼리파라미터라면 이 부분만 ParameterIn.QUERY로 바뀜.
            )
        })
    public ResponseEntity<ApiResponse<UserDto>> getUserInfo(@LoginUser CustomUserDetails loginUser,
        @Parameter(description = "사용자 ID") @PathVariable("userId") Long userId) {
        var userDto = userService.getUserInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }

    @GetMapping
    public List<UserDto> getAllUserTestExample() {

        return userService.getAllUserTestExample();
    }

    @Operation(
        summary = "메모 수정",
        description = "메모 정보 수정",
        security = @SecurityRequirement(name = JWT),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "메모 수정 JSON 데이터",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MemoRequestDto.class)
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = "{\"id\":1}"
                    )
//                    schema = @Schema(implementation = MemoRequestDto.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "실패",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MemoRequestDto.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "실패",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MemoRequestDto.class)
                )
            )
        })
    @PatchMapping("/memo")
    public ResponseEntity<ApiResponse<Void>> updateMemo(@LoginUser CustomUserDetails loginUser,
        @RequestBody MemoRequestDto requestDto) {
        userService.updateMemo(loginUser.getUserId(), requestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @Operation(
        summary = "마이페이지 기본 정보 조회",
        description = "로그인한 사용자의 마이페이지 기본 정보(프로필, 파트너, 설정)를 조회합니다.",
        security = @SecurityRequirement(name = JWT)
    )
    @GetMapping("/myPage")
    public ResponseEntity<ApiResponse<MyPageMainResponseDto>> getMyPageInfo(
        @LoginUser CustomUserDetails loginUser) {
        MyPageMainResponseDto responseDto = userService.getMyPageInfo(loginUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @Operation(
        summary = "마이페이지 사진(이미지) 삭제 -> 기본 이미지로 변경",
        description = "로그인한 사용자의 설정된 사진 삭제",
        security = @SecurityRequirement(name = JWT)
    )
    @DeleteMapping("/image")
    public void deleteProfileImage(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "마이페이지 사진(이미지) 수정",
        description = "로그인한 사용자의 설정된 사진 수정",
        security = @SecurityRequirement(name = JWT)
    )
    @PostMapping("/image")
    public void changeProfileImage(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "파트너 이미지 조회",
        description = "로그인한 사용자의 설정된 파트너 사진 및 파트너 컬러 조회",
        security = @SecurityRequirement(name = JWT)
    )
    @GetMapping("/partnerImage")
    public void getPartnerImage(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "파트너 연결 끊기 ",
        description = "로그인한 사용자의 설정된 파트너 연결 끊기",
        security = @SecurityRequirement(name = JWT)
    )
    @DeleteMapping("/partner")
    public void deletePartner(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "마이페이지 계정관리 ",
        description = "자신의 닉네임,이메일,프로필 이미지 조회",
        security = @SecurityRequirement(name = JWT)
    )
    @GetMapping("/myAccount")
    public void getMyAccountInfo(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "로그아웃 ",
        description = "자신 로그아웃",
        security = @SecurityRequirement(name = JWT)
    )
    @GetMapping("/myAccount")
    public void logout(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "계정삭제 ",
        description = "자신 계정삭제",
        security = @SecurityRequirement(name = JWT)
    )
    @DeleteMapping("/myAccount")
    public void deleteMyAccount(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "닉네임변경 ",
        description = "자신 닉네임변경",
        security = @SecurityRequirement(name = JWT)
    )
    @PatchMapping("/myAccount")
    public void updateNickName(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "컬러 설정조회 ",
        description = "자신과파트너 우리 개인이 설정한 컬러 설정 조회",
        security = @SecurityRequirement(name = JWT)
    )
    @GetMapping("/colorConfig")
    public void getColorConfig(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "컬러 설정 업데이트 ",
        description = "자신과 파트너의 개인 컬러 설정 업데이트",
        security = @SecurityRequirement(name = JWT)
    )
    @GetMapping("/colorConfig")
    public void updateColorConfig(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "결혼 예정일 조회 ",
        description = "결혼 예정일 조회",
        security = @SecurityRequirement(name = JWT)
    )
    @GetMapping("/weddingDate")
    public void getWeddingDate(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "결혼 예정일 수정 ",
        description = "결혼 예정일 수정",
        security = @SecurityRequirement(name = JWT)
    )
    @PatchMapping("/weddingDate")
    public void updateWeddingDate(@LoginUser CustomUserDetails loginUser) {

    }

    @Operation(
        summary = "알림 설정 조회 ",
        description = "알림 설정 조회",
        security = @SecurityRequirement(name = JWT)
    )
    @PatchMapping("/weddingDate")
    public void getAlarmConfig(@LoginUser CustomUserDetails loginUser) {

    }
}
