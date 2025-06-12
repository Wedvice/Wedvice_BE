package com.wedvice.couple.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchCodeService {
    private final MatchCodeGenerator matchCodeGenerator;
    private Map<String, MatchCode> codeMap;

    @Autowired
    public MatchCodeService(MatchCodeGenerator matchCodeGenerator) {
        this.matchCodeGenerator = matchCodeGenerator;
        this.codeMap = new ConcurrentHashMap<>();
    }


    public String generateCode(Long userId) {
        String code = matchCodeGenerator.generateUniqueCode(codeMap.keySet()); // 예: 무서운아몬드123
        codeMap.put(code, new MatchCode(userId, LocalDateTime.now()));
        return code;
    }

    public Optional<Long> consumeCode(String code) {
        MatchCode matchCode = codeMap.remove(code);
        if (matchCode == null) return Optional.empty();
        return Optional.of(matchCode.getUserId());
    }

    public boolean isValid(String code) {
        MatchCode mc = codeMap.get(code);
        return mc != null && !isExpired(mc.getCreatedAt());
    }

    private boolean isExpired(LocalDateTime createdAt) {
        return Duration.between(createdAt, LocalDateTime.now()).toMinutes() > 10;
    }

    public void removeExpiredCodes() {
        codeMap.entrySet().removeIf(entry -> isExpired(entry.getValue().getCreatedAt()));
    }

    @PostConstruct
    private void makeTestUserCode() {
        String testCode = generateCode(1000000L);
        System.out.println(testCode);
    }

}
