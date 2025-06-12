package com.wedvice.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "user_alarm_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAlarmConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userConfig_id")
    private UserConfig userConfig;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmTimeSetting alarmTimeSetting; // ALWAYS, DAY_ONLY, NONE

    @Column(nullable = false)
    private boolean baseAlarm;

    @Column(nullable = false)
    private boolean commentAlarm;

    @Column(nullable = false)
    private boolean scheduleAlarm;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    enum AlarmTimeSetting {
        ALWAYS, DAY_ONLY, NONE
    }

}