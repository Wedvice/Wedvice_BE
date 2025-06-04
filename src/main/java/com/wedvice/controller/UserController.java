package com.wedvice.controller;

import com.wedvice.dto.UserDto;
import com.wedvice.entity.User;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "👤 사용자 관련 API (메모 업데이트, 매칭, 사용자 정보 업데이트 등)")
public class UserController {

    private final UserService userService;

    @GetMapping("/get/{userId}")
    @Operation(summary = "📝 간단한 유저 정보 불러오기(테스트)", description = "사용자 id 입력 시, 간단한 정보 가져옴")
    public ResponseEntity<UserDto> getUserInfo(@LoginUser CustomUserDetails loginUser, @Parameter(description = "사용자 ID") @PathVariable("userId") Long userId) {
        var userDto = userService.getUserInfo(userId);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/update/{userId}")
    @Operation(summary = "📝 사용자 정보 업데이트", description = "사용자의 닉네임, 매칭된 사용자 ID, 메모를 업데이트합니다. 각 파라미터는 선택적으로 입력할 수 있습니다.")
    public ResponseEntity<String> updateUserInfo(
            @Parameter(description = "업데이트할 사용자 ID", example = "1") @PathVariable Long userId,
            @Parameter(description = "업데이트할 닉네임", example = "강승수") @RequestParam(required = false) String nickname,
            @Parameter(description = "매칭된 사용자의 ID", example = "2") @RequestParam(required = false) Long matchedUserId,
            @Parameter(description = "업데이트할 메모 내용", example = "우리 결혼 날짜 정하자!") @RequestParam(required = false) String memo) {

        userService.updateUserInfo(userId, nickname, matchedUserId, memo);
        return ResponseEntity.ok("사용자 정보가 업데이트되었습니다.");
    }

    @PatchMapping("/update-memo/{userId}")
    @Operation(summary = "📝 사용자 메모 업데이트", description = "특정 사용자의 메모를 업데이트합니다.")
    public ResponseEntity<String> updateMemo(
            @Parameter(description = "메모를 업데이트할 사용자 ID", example = "1") @PathVariable Long userId,
            @Parameter(description = "업데이트할 메모 내용", example = "우리 결혼식 준비 시작하자!") @RequestParam String memo) {

        userService.updateMemo(userId, memo);
        return ResponseEntity.ok("메모가 성공적으로 업데이트되었습니다.");
    }

    @PatchMapping("/update-matched/{userId}")
    @Operation(summary = "💑 사용자 매칭 및 커플 생성", description = "한 사용자의 매칭 ID를 업데이트하며, 상대방의 매칭 ID도 자동으로 업데이트됩니다. 또한 새로운 커플 레코드가 Couple 테이블에 생성됩니다.")
    public ResponseEntity<String> updateMatchedUser(
            @Parameter(description = "매칭을 업데이트할 사용자 ID", example = "1") @PathVariable Long userId,
            @Parameter(description = "매칭할 상대방 사용자 ID", example = "2") @RequestParam Long matchedUserId) {

        userService.updateMatchedUserAndCreateCouple(userId, matchedUserId);
        return ResponseEntity.ok("매칭된 사용자 정보가 양방향으로 업데이트되고 커플이 생성되었습니다.");
    }

    @GetMapping("/{userId}")
    @Operation(summary = "👀 사용자 정보 조회", description = "지정된 사용자 ID로 사용자 정보를 조회합니다.")
    public ResponseEntity<User> getUser(
            @Parameter(description = "조회할 사용자 ID", example = "1") @PathVariable Long userId) {
        User user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }
}
