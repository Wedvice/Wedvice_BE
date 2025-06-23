package com.wedvice.couple.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MatchCodeServiceTest {
    @Mock
    private MatchCodeGenerator matchCodeGenerator;

    @InjectMocks
    private MatchCodeService matchCodeService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // codeMap 초기화 (매 테스트마다 깨끗한 상태로)
        Field codeMapField = MatchCodeService.class.getDeclaredField("codeMap");
        codeMapField.setAccessible(true);
        codeMapField.set(matchCodeService, new ConcurrentHashMap<>());
    }

    // ============================== 헬퍼 메서드 ==============================
    private void setCodeMapEntry(String code, MatchCode value) throws Exception {
        Field codeMapField = MatchCodeService.class.getDeclaredField("codeMap");
        codeMapField.setAccessible(true);
        ConcurrentHashMap<String, MatchCode> map = (ConcurrentHashMap<String, MatchCode>) codeMapField.get(matchCodeService);
        map.put(code, value);
    }

    // ============================== 정상 흐름 ==============================
    @Nested
    @DisplayName("정상 흐름: generateCode() 호출 시")
    class GenerateCodeTests {

        @Test
        @DisplayName("코드는 유효한 형식으로 생성되고, 사용자 ID와 연결되어야 한다.")
        void shouldGenerateCode_AndLinkToUserId() {
            // given
            Long userId = 123L;
            String expectedCode = "귀여운토끼123";
            given(matchCodeGenerator.generateUniqueCode(any())).willReturn(expectedCode);

            // when
            String code = matchCodeService.generateCode(userId);

            // then
            assertAll("코드 생성 결과 확인",
                    () -> assertNotNull(code, "코드는 null이면 안 된다."),
                    () -> assertEquals(expectedCode, code, "예상된 코드가 생성되어야 한다."),
                    () -> assertTrue(matchCodeService.isValid(code), "생성된 코드는 유효해야 한다."),
                    () -> assertTrue(matchCodeService.getCodeUserId(code).isPresent(), "ID 조회가 가능해야 한다."),
                    () -> assertEquals(userId, matchCodeService.getCodeUserId(code).get(), "ID가 일치해야 한다.")
            );
        }
    }

    // ============================== 코드 만료 관련 ==============================
    @Nested
    @DisplayName("코드가 만료된 경우")
    class ExpiredCodeTests {

        @Test
        @DisplayName("만료된 코드는 유효하지 않으며, ID를 조회할 수 없다.")
        void shouldInvalidateExpiredCode() throws Exception {
            // given
            String code = "만료된코드123";
            MatchCode expired = new MatchCode(999L, LocalDateTime.now().minusMinutes(11));
            setCodeMapEntry(code, expired);

            // when & then
            assertAll("만료 코드 처리",
                    () -> assertFalse(matchCodeService.isValid(code), "만료되었음에도 유효 판정"),
                    () -> assertTrue(matchCodeService.getCodeUserId(code).isEmpty(), "만료된 코드에서 ID 조회 발생")
            );
        }
    }

    // ============================== 코드 제거 관련 ==============================
    @Nested
    @DisplayName("코드 제거 시")
    class RemoveCodeTests {

        @Test
        @DisplayName("removeCode 호출 후에는 해당 코드로 ID 조회가 불가능해야 한다.")
        void shouldNotRetrieveIdAfterCodeRemoval() throws Exception {
            // given
            String code = "삭제코드123";
            setCodeMapEntry(code, new MatchCode(2L, LocalDateTime.now()));

            // when
            matchCodeService.removeCode(code);

            // then
            assertFalse(matchCodeService.getCodeUserId(code).isPresent(), "코드 제거 후에도 ID가 조회됨");
        }
    }

    // ============================== 만료 코드 정리 관련 ==============================
    @Nested
    @DisplayName("만료된 코드 정리 메서드 호출 시")
    class RemoveExpiredCodesTests {

        @Test
        @DisplayName("만료된 코드는 제거되고, 유효한 코드는 유지되어야 한다.")
        void shouldRemoveOnlyExpiredCodes() throws Exception {
            // given
            String expiredCode = "코드1";
            String validCode = "코드2";
            setCodeMapEntry(expiredCode, new MatchCode(1L, LocalDateTime.now().minusMinutes(11)));
            setCodeMapEntry(validCode, new MatchCode(2L, LocalDateTime.now()));

            // when
            matchCodeService.removeExpiredCodes();

            // then
            assertAll("코드 정리 검증",
                    () -> assertFalse(matchCodeService.isValid(expiredCode), "만료 코드가 제거되지 않음"),
                    () -> assertTrue(matchCodeService.isValid(validCode), "유효한 코드가 잘못 제거됨")
            );
        }
    }
}