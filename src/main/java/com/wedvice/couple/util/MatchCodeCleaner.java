package com.wedvice.couple.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MatchCodeCleaner {

    private final MatchCodeService matchCodeService;

    public MatchCodeCleaner(MatchCodeService matchCodeService) {
        this.matchCodeService = matchCodeService;
    }

    @Scheduled(fixedRate = 60000) // 1분마다
    public void cleanExpiredCodes() {
        log.info("cleanExpiredCodes active");
        matchCodeService.removeExpiredCodes();
    }
}