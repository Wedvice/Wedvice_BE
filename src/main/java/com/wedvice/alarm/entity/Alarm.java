package com.wedvice.alarm.entity;

import com.wedvice.alarm.type.AlarmType;
import com.wedvice.common.BaseTimeEntity;
import com.wedvice.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Alarm")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    private String content;

    private AlarmType alarmType;

    @Column(nullable = false)
    private boolean read;

//    @Column(columnDefinition = "json")
//    @Convert(converter = AlarmParamsConverter.class)
//    private AlarmParams params;
//
//    @Column(columnDefinition = "json")
//    @Convert(converter = AlarmDataConverter.class)
//    private AlarmData data;

    /**
     * 메서드 시작
     */

    // private 생성자 (빌더 패턴용)
    @Builder(access = AccessLevel.PRIVATE)
    private Alarm(User user, String title, String content, AlarmType alarmType) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.read = false;
        this.alarmType = alarmType;
    }

    // 정적 팩토리 메서드
    public static Alarm create(User user, String title, String content, AlarmType alarmType) {
        return Alarm.builder()
            .user(user)
            .title(title)
            .content(content)
            .alarmType(alarmType)
            .build();
    }

    public void read() {
        this.read = true;
    }
}
