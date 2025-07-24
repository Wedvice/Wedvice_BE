package com.wedvice.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.JwtAuthenticationFilter;
import com.wedvice.security.login.JwtTokenProvider;
import com.wedvice.security.login.LoginUserArgumentResolver;
import com.wedvice.task.dto.DeleteTasksRequestDto;
import com.wedvice.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtTokenProvider.class)
        }
)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private LoginUserArgumentResolver loginUserArgumentResolver;

    @BeforeEach
    void setUp() throws Exception {
        // Mocking the behavior of LoginUserArgumentResolver for all tests that need it
        when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(new CustomUserDetails(1L, "test@example.com", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("유효한 요청으로 Task를 성공적으로 삭제해야 한다")
    void shouldReturnOkWhenDeletingTasksSuccessfully() throws Exception {
        // Given
        List<Long> taskIds = Arrays.asList(1L, 2L);
        DeleteTasksRequestDto requestDto = new DeleteTasksRequestDto();
        requestDto.setTaskIds(taskIds);

        doNothing().when(taskService).deleteTasks(anyList(), any(CustomUserDetails.class));

        // When & Then
        mockMvc.perform(delete("/api/task")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(taskService, times(1)).deleteTasks(eq(taskIds), any(CustomUserDetails.class));
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 Task 삭제 요청을 할 수 없어야 한다")
    void shouldRedirectWhenNotAuthenticated() throws Exception {
        // Given
        List<Long> taskIds = Arrays.asList(1L, 2L);
        DeleteTasksRequestDto requestDto = new DeleteTasksRequestDto();
        requestDto.setTaskIds(taskIds);

        // When & Then
        mockMvc.perform(delete("/api/task")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isFound()); // Expect 302 Found (Redirect)

        verify(taskService, never()).deleteTasks(anyList(), any(CustomUserDetails.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("유효하지 않은 입력(빈 리스트)에 대해 Bad Request를 반환해야 한다")
    void shouldReturnBadRequestForEmptyList() throws Exception {
        // Given
        DeleteTasksRequestDto requestDto = new DeleteTasksRequestDto();
        requestDto.setTaskIds(Collections.emptyList());

        // When & Then
        mockMvc.perform(delete("/api/task")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        // Verify that the service is never called
        verify(taskService, never()).deleteTasks(any(), any(CustomUserDetails.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("서비스에서 예외 발생 시 Bad Request를 반환해야 한다")
    void shouldReturnBadRequestWhenServiceThrowsException() throws Exception {
        // Given
        List<Long> taskIds = Arrays.asList(99L); // Non-existent ID
        DeleteTasksRequestDto requestDto = new DeleteTasksRequestDto();
        requestDto.setTaskIds(taskIds);

        doThrow(new RuntimeException("Task not found")).when(taskService).deleteTasks(anyList(), any(CustomUserDetails.class));

        // When & Then
        mockMvc.perform(delete("/api/task")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(taskService, times(1)).deleteTasks(eq(taskIds), any(CustomUserDetails.class));
    }
}
