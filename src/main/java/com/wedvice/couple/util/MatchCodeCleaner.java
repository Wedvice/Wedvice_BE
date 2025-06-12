package com.wedvice.couple.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MatchCodeCleaner {

    private final MatchCodeService matchCodeService;

    public MatchCodeCleaner(MatchCodeService matchCodeService) {
        this.matchCodeService = matchCodeService;
    }

    @Scheduled(fixedRate = 60000) // 1분마다
    public void cleanExpiredCodes() {
        matchCodeService.removeExpiredCodes();
    }

}