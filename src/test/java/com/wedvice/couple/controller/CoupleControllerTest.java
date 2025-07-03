//package com.wedvice.couple.controller;
//
//import com.wedvice.common.ApiResponse;
//import com.wedvice.couple.dto.CoupleHomeInfoResponseDto;
//import com.wedvice.couple.service.CoupleService;
//import com.wedvice.couple.util.MatchCodeService;
//import com.wedvice.security.login.CustomUserDetails;
//import com.wedvice.security.login.JwtTokenProvider;
//import com.wedvice.user.entity.User;
//import com.wedvice.user.service.UserService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.*;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.DefaultResponseErrorHandler;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@ActiveProfiles("test")
//class CoupleControllerTest {
//
//
//    private static final Logger log = LoggerFactory.getLogger(CoupleControllerTest.class);
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    CoupleService coupleService;
//
//    @MockBean
//    UserService userService;
//
//    @MockBean
//    MatchCodeService matchCodeService;
//
//    @MockBean
//    private JwtTokenProvider jwtTokenProvider;
//
//
//
////    @BeforeEach
////    void setupJwtMock() {
////        given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
////        given(jwtTokenProvider.getUserId(anyString())).willReturn("11111");
////    }
//
//
////    @BeforeEach
////    void setupAuthenticatedUser() {
////
////
////        CustomUserDetails userDetails = new CustomUserDetails(11111L, "kakaoUser", null);
////        UsernamePasswordAuthenticationToken authentication =
////                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
////
////        SecurityContextHolder.getContext().setAuthentication(authentication);
////    }
////
////    @AfterEach
////    void clearSecurityContext() {
////        SecurityContextHolder.clearContext();
////    }
//
//
//
//    @Test
//    void getMatchCode_성공() throws Exception {
//
//        // given
//        String url = "http://localhost:8080" + "/api/couple/match-code";
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwib2F1dGhJZCI6IjEyMzQ1Njc4OSIsImlhdCI6MTc1MDM4NjM4NCwiZXhwIjoxNzUwMzg4MTg0fQ.jvu5L-Xdvf3QApE9WPpimqyzbAfbQg6oifwrnhpDBX8");
//
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        // when
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
//
//        // then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        System.out.println("응답: " + response.getBody());
//
//
//    }
//
////    토큰을 계속 갈아줘야한다는게 , 그리고 실서버에 진짜 요청을 보내본다는거 , 테스트 개선사항.
//    @Test
//    void match_실패() throws Exception {
//
//        // given
//        String url = "http://localhost:8080/api/couple/match";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwib2F1dGhJZCI6IjEyMzQ1Njc4OSIsImlhdCI6MTc1MDM4ODI3NSwiZXhwIjoxNzUwMzkwMDc1fQ.81jb64SBr_TDIbbaxQKkg9oHrQNFX-YsqpYrsxAYF9Y");
//
//        // create request body
////                "matchCode": "123456789012345678901234567890123456789012345678901234567890"
//
//        String body = """
//            {
//                "matchCode": "무서운호랑이231"
//            }
//            """;
//
//        HttpEntity<String> request = new HttpEntity<>(body, headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        // 🔧 예외 발생 방지 설정
//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
//            @Override
//            public boolean hasError(ClientHttpResponse response) throws IOException {
//                return false; // 모든 상태 코드에 대해 예외 발생 안 시킴
//            }
//        });
//
//        log.info("========");
//        // when
//        ResponseEntity<ApiResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, ApiResponse.class);
//        log.info("========");
//
//
//        System.out.println("HttpStatus.BAD_REQUEST.value() = " + HttpStatus.BAD_REQUEST.value());
//        System.out.println("response.getStatusCode().value() = " + response.getStatusCode().value());
//
//        // then
//        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
//        System.out.println("응답: " + response.getBody());
//
//
//    }
//
//
////    @Test
////    void getMatchCode() {
////
////        User kakaoUser = User.builder().oauthId("11111").provider("kakao").build();
////        User naverUser = User.builder().oauthId("22222").provider("naver").build();
////
////
////
////    }
//
////    @WithMockUser(username = "1", roles = "USER") // SecurityContext에 인증 주입
////    @Test
////
//////    로그인이 되어서 jwtToken을 보내줘야한다?
//////    커플이 매칭된 상태여야한다
//////
////
////
////    void getCoupleInfo_인증된사용자_성공() throws Exception {
////        given(coupleService.getCoupleInfo(anyLong()))
////                .willReturn(new CoupleHomeInfoResponseDto(...));
////
////        mockMvc.perform(get("/api/couple/summary"))
////                .andExpect(status().isOk());
////    }
//
////    @Test
////    void completeMatch() {
////    }
////
////    @Test
////    void getCoupleInfo() {
////    }
//
//
//}