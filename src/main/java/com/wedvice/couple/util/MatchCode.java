package com.wedvice.couple.util;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MatchCode {
    private final long userId;
    private final LocalDateTime createdAt;

    public MatchCode(long userId, LocalDateTime createdAt) {
        this.userId = userId;
        this.createdAt = createdAt;
    }
}
