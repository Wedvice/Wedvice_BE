package com.wedvice.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean pushEnabled;

    private LocalDateTime weddingDate;

    @Enumerated(EnumType.STRING)
    private Color myColor; // 예: "light", "dark", "purple" 등
    @Enumerated(EnumType.STRING)
    private Color yourColor;
    @Enumerated(EnumType.STRING)
    private Color ourColor;

    private String language;   // 예: "ko", "en"

    private boolean alarmVibration;

    private boolean notificationSound;


    public enum Color {
        RED,
        YELLOW,
        GREEN,
        BLUE,
        PURPLE,
        RIGHT_DARK,
        PINK,
        NOT_IMPLEMENT
    }

}
