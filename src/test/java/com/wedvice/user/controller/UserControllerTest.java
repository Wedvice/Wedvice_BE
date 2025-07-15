package com.wedvice.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.JwtAuthenticationFilter;
import com.wedvice.user.dto.MemoRequestDto;
import com.wedvice.user.dto.UserDto;
import com.wedvice.user.service.UserService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
    })
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserDetails userDetails = new CustomUserDetails(1L, "testuser@example.com",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities())
        );
    }

    @Test
    @DisplayName("유저 정보 조회 성공")
    void getUserInfo_success() throws Exception {
        // given
        Long userId = 1L;
        UserDto userDto = UserDto.builder().id(userId).nickname("테스").build();
        given(userService.getUserInfo(anyLong())).willReturn(userDto);

        // when & then
        mockMvc.perform(get("/api/user/{userId}", userId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(userId))
            .andExpect(jsonPath("$.data.nickname").value("테스"));
    }

    @Test
    @DisplayName("모든 유저 정보 조회 성공")
    void getAllUserTestExample_success() throws Exception {
        // given
        List<UserDto> userList = List.of(
            UserDto.builder().id(1L).nickname("유1").build(),
            UserDto.builder().id(2L).nickname("유2").build()
        );
        given(userService.getAllUserTestExample()).willReturn(userList);

        // when & then
        mockMvc.perform(get("/api/user"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].nickname").value("유1"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].nickname").value("유2"));
    }

    @Test
    @DisplayName("메모 수정 성공")
    void updateMemo_success() throws Exception {
        // given
        Long loginUserId = 1L;
        String newMemoContent = "새로운 메모 내용";
        MemoRequestDto requestDto = new MemoRequestDto(newMemoContent);

        doNothing().when(userService).updateMemo(anyLong(), any(MemoRequestDto.class));

        // when & then
        mockMvc.perform(patch("/api/user/memo")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        // verify
        verify(userService).updateMemo(anyLong(), any(MemoRequestDto.class));
    }
}