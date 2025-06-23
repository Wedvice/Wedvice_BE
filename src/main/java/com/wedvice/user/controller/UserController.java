package com.wedvice.user.controller;

import com.wedvice.common.ApiResponse;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.user.dto.UserDto;
import com.wedvice.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "👤 사용자 관련 API (메모 업데이트, 매칭, 사용자 정보 업데이트 등)")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @Operation(summary = "📝 간단한 유저 정보 불러오기(테스트)", description = "사용자 id 입력 시, 간단한 정보 가져옴")
    public ResponseEntity<ApiResponse<UserDto>> getUserInfo(@LoginUser CustomUserDetails loginUser, @Parameter(description = "사용자 ID") @PathVariable("userId") Long userId) {
        var userDto = userService.getUserInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }

    @GetMapping
    public List<UserDto> getAllUserTestExample(){

        return userService.getAllUserTestExample();
    }
}
