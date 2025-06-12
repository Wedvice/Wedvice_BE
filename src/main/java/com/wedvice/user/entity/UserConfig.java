package com.wedvice.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    private Color myColor; // 예: "light", "dark", "purple" 등
    private Color yourColor;
    private Color ourColor;

    private String language;   // 예: "ko", "en"

    private boolean alarmVibration;

    private boolean notificationSound;


    enum Color {
        RED,
        YELLOW,
        GREEN,
        BLUE,
        PURPLE,
        RIGHT_DARK,
        PINK,

    }

}
