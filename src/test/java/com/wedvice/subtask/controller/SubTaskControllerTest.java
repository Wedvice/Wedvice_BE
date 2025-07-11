package com.wedvice.subtask.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wedvice.coupletask.service.CoupleTaskService;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUserArgumentResolver;
import com.wedvice.subtask.service.SubTaskService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class SubTaskControllerTest {

    @InjectMocks
    private SubTaskController subTaskController;

    @Mock
    private SubTaskService subTaskService;

    @Mock
    private CoupleTaskService coupleTaskService;

    @Mock
    private LoginUserArgumentResolver loginUserArgumentResolver;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(subTaskController)
            .setCustomArgumentResolvers(loginUserArgumentResolver)
            .build();

        when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
            .thenReturn(new CustomUserDetails(1L, "test@example.com", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    @Test
    @DisplayName("모든 커플 태스크의 가격을 조회한다")
    void getAllCoupleTaskPrice() throws Exception {
        // given
        CustomUserDetails mockUserDetails = new CustomUserDetails(1L, "test@example.com", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(coupleTaskService.getCoupleTasksWithSubTaskInfo(anyLong())).thenReturn(Collections.emptyList());
        // when
        mockMvc.perform(get("/api/subtask/price"))
            .andExpect(status().isOk());

        // then
        verify(coupleTaskService).getCoupleTasksWithSubTaskInfo(mockUserDetails.getUserId());
    }
}
