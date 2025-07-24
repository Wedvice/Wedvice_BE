package com.wedvice.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedvice.security.login.JwtTokenProvider;
import com.wedvice.user.dto.MemoRequestDto;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성 및 DB 저장
        User userToSave = User.createForTestWithId(1L, "oauthId123", "kakao", "통테", "테스트메모");
        testUser = userRepository.save(userToSave);

        accessToken = tokenProvider.generateAccessToken(testUser.getId().toString(),
            testUser.getNickname(), testUser.getOauthId());

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

    @Test
    @DisplayName("유효하지 않은 토큰으로 유저 정보 조회 시 401 에러 발생")
    void getUserInfo_with_invalid_token_fails() throws Exception {
        // given
        Long userId = testUser.getId();
        String invalidToken = "this.is.invalid.token";

        // when & then
        mockMvc.perform(get("/api/user/{userId}", userId)
                .header("Authorization", "Bearer " + invalidToken))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("메모 수정 통합 테스트 성공")
    void updateMemo_integration_success() throws Exception {
        // given
        String newMemo = "새로운 메모입니다.";
        MemoRequestDto requestDto = new MemoRequestDto(newMemo);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        mockMvc.perform(patch("/api/user/memo")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        // then
        // 영속성 컨텍스트의 변경 내용을 DB에 강제 반영하고, 컨텍스트를 비워 DB에서 새로 조회하도록 함
        entityManager.flush();
        entityManager.clear();

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals(newMemo, updatedUser.getMemo());
    }
}
