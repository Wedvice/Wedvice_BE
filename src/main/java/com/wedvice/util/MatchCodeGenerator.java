package com.wedvice.util;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Set;

@Component
public class MatchCodeGenerator {

    private static final String[] ADJECTIVES = {
            "무서운", "귀여운", "멋진", "시끄러운", "고요한",
            "빠른", "느린", "달콤한", "차가운", "뜨거운"
    };

    private static final String[] NOUNS = {
            "아몬드", "토끼", "호랑이", "곰", "하마",
            "기린", "독수리", "햄스터", "판다", "펭귄"
    };

    private static final int MAX_RETRY = 10;
    private final Random random = new Random();

    public String generateUniqueCode(Set<String> existingCodes) {
        for (int i = 0; i < MAX_RETRY; i++) {
            String code = generateRawCode();
            if (!existingCodes.contains(code)) {
                return code;
            }
        }
        throw new RuntimeException("고유한 매칭 코드를 생성할 수 없습니다.");
    }

    private String generateRawCode() {
        String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];
        int number = 100 + random.nextInt(900); // 100~999
        return adjective + noun + number;
    }

    public boolean isValidFormat(String code) {
        return code.matches("^[가-힣]{2,4}[가-힣]{2,4}\\d{3}$");
        // 예: 귀여운토끼123, 멋진펭귄456 등
    }
}