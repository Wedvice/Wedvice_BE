package com.wedvice.security.login;

import com.wedvice.common.ApiResponse;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import com.wedvice.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;


    //    필요한지?
    @GetMapping("/status")
    public String loginStatus() {

        String response = "";

        log.info("======"+SecurityContextHolder.getContext().getAuthentication());
        log.info("++++"+SecurityContextHolder.getContext());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info((authentication != null)+"");

        log.info(authentication.isAuthenticated()+"");
        log.info(authentication.getPrincipal() instanceof CustomUserDetails +"");
        log.info(authentication.getPrincipal().toString());

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
             response += "일단 로그인 완료" + customUserDetails+"\n";
        } else {
            response += "일단 로그인 아님\n";
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findById(customUserDetails.getUserId()).orElseThrow();

        if (user.getCouple() == null) {
            return response += "매칭 필요";
        }


        if(user.getNickname() == null){

            return response += " 닉네임 입력필요";
        }

        if (user.getRole() == null) {
            return response += "성별입력필요";
        }

        Optional<User> matchedUser = user.getCouple().getUsers().stream()
                .filter(anotherUser -> !anotherUser.getId().equals(user.getId()))
                .findFirst();

        User matchingUser = matchedUser.get();

        if(matchingUser.getRole()!=null && matchingUser.getNickname()!=null){

            return response += "홈 페이지 이동 ";

        }

        return "예시 -> 뭔가 알수없는 에러";

    }

    @GetMapping("/redirect")
    @Operation(
            summary = "로그인 후 리다이렉트 정보 조회",
            description = "현재 본인의 회원가입 진행 상태를 조회합니다."
    )
    public ResponseEntity<ApiResponse<RedirectResponseDto>> getLoginStatus(@LoginUser CustomUserDetails loginUser) {
        RedirectResponseDto redirectResponseDto = userService.getRedirectStatus(loginUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(redirectResponseDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refreshToken", required = false) Cookie cookie) {

        Map<String, Object> result = userService.refresh(cookie);
        HttpHeaders headers = (HttpHeaders) result.get("headers");
        Object body = result.get("body");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(body);
    }
}
