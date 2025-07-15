package com.wedvice.user.controller;

import static org.springframework.security.config.Elements.JWT;

import com.wedvice.common.ApiResponse;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.user.dto.MemoRequestDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PatchMapping("/memo")
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
            )
        })
    public ResponseEntity<ApiResponse<Void>> updateMemo(@LoginUser CustomUserDetails loginUser,
        MemoRequestDto requestDto) {
        userService.updateMemo(loginUser.getUserId(), requestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
