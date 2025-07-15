package com.wedvice.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.JwtTokenProvider;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String accessToken;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성 및 DB 저장
        testUser = User.createForTestWithId(1L, "oauthId123", "kakao", "통테", "테스트메모");
        userRepository.save(testUser);

        accessToken = tokenProvider.generateAccessToken(testUser.getId().toString(),
            testUser.getNickname(), testUser.getOauthId());

        // @LoginUser 어노테이션을 위한 가짜 인증 정보 설정
        CustomUserDetails userDetails = new CustomUserDetails(testUser.getId(),
            testUser.getOauthId(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities())
        );
    }

    @Test
    @DisplayName("유저 정보 조회 통합 테스트 성공")
    void getUserInfo_integration_success() throws Exception {
        // given
        Long userId = testUser.getId();

        // when & then
        mockMvc.perform(get("/api/user/{userId}", userId)
                .header("Authorization", "Bearer " + accessToken))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(userId))
            .andExpect(jsonPath("$.data.nickname").value(testUser.getNickname()))
            .andExpect(jsonPath("$.data.memo").value(testUser.getMemo()));
    }
}
