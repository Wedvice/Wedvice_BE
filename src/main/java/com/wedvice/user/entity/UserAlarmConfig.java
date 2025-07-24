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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;


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
    @JoinColumn(name = "user_config_id")
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