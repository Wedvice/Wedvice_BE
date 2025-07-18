package com.wedvice.user.entity;

import jakarta.persistence.Column;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_configs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_config_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    private LocalDateTime weddingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "my_color")
    private Color myColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "partner_color")
    private Color partnerColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "our_color", updatable = false)
    private Color ourColor;

    private String language;   // 예: "ko", "en"

    private boolean alarmVibration;

    private boolean notificationSound;

    private boolean pushEnabled;

    /**
     * 메서드 시작
     **/

    // private 생성자 (빌더 패턴용)
    @Builder(access = AccessLevel.PRIVATE)
    private UserConfig(Long id, User user, boolean pushEnabled, LocalDateTime weddingDate,
        Color myColor, Color partnerColor, Color ourColor, String language, boolean alarmVibration,
        boolean notificationSound) {
        this.id = id;
        this.user = user;
        this.pushEnabled = pushEnabled;
        this.weddingDate = weddingDate;
        this.myColor = myColor;
        this.partnerColor = partnerColor;
        this.ourColor = ourColor;
        this.language = language;
        this.alarmVibration = alarmVibration;
        this.notificationSound = notificationSound;
    }

    // 정적 팩토리 메서드: User와 함께 기본 설정값을 가진 UserConfig 생성
    public static UserConfig createDefaultConfig(User user) {
        UserConfig userConfig = UserConfig.builder()
            .user(user)
            .pushEnabled(true)
            .alarmVibration(true)
            .notificationSound(true)
            .language("ko")
            .myColor(Color.BLUE) // 임의의 기본 색상
            .partnerColor(Color.RED) // 임의의 기본 색상
            .ourColor(Color.GREEN) // 임의의 기본 색상
            .build();

        user.addUserConfig(userConfig);

        return userConfig;
    }


    // 업데이트 메서드
    public void updateMyColor(Color myColor) {
        this.myColor = myColor;
    }

    public void updatePartnerColor(Color partnerColor) {
        this.partnerColor = partnerColor;
    }

    public void updateLanguage(String language) {
        this.language = language;
    }

    public void updateAlarmVibration(boolean alarmVibration) {
        this.alarmVibration = alarmVibration;
    }

    public void updateNotificationSound(boolean notificationSound) {
        this.notificationSound = notificationSound;
    }

    public void updatePushEnabled(boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public void updateWeddingDate(LocalDateTime weddingDate) {
        this.weddingDate = weddingDate;
    }

    @Getter
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
