package com.wedvice.user.entity;

import com.wedvice.common.BaseTimeEntity;
import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.exception.NotMatchedYetException;
import com.wedvice.couple.exception.PartnerNotFoundException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String oauthId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = true, length = 10)
    private String nickname;

    private String profileImageUrl;

    @Column(nullable = true)
    private String memo;

    @Column(nullable = true)
    private String refreshToken;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = true)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id", nullable = true)
    private Couple couple;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserConfig userConfig;


    /**
     * 메서드 시작
     */

    // private 생성자 (빌더 패턴용)
    @Builder(access = AccessLevel.PRIVATE)
    private User(String oauthId, String provider, String nickname, String profileImageUrl,
        String memo, String refreshToken, String email, Role role) {
        this.oauthId = oauthId;
        this.provider = provider;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.memo = memo;
        this.refreshToken = refreshToken;
        this.email = email;
        this.role = role;
    }


    // 정적 팩토리 메서드
    public static User create(String oauthId, String provider) {
        return User.builder()
            .oauthId(oauthId)
            .provider(provider)
            .role(Role.USER) // 기본 역할 유저 -> 매칭안된상태
            .build();
    }

    // 연관관계 편의 메서드
    public void matchCouple(Couple couple) {
        this.couple = couple;
        couple.getUsers().add(this);
    }

    // 업데이트 메서드
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }

    public void updateRole(User.Role role) {
        this.role = role;
    }


    // 도메인 내부: 상태 판단만 담당
    public boolean isMatched() {
        return this.couple != null;
    }

    public boolean isInfoCompleted() {
        return this.nickname != null && this.role != null;
    }

    public boolean isPartnerInfoCompleted() {
        try {
            return getPartnerOrThrow().isInfoCompleted();
        } catch (PartnerNotFoundException e) {
            return false; // 파트너가 없으면 info도 당연히 불완전
        }
    }

    public User getPartnerOrThrow() {
        if (this.couple == null) {
            throw new NotMatchedYetException();
        }

        return this.couple.getUsers().stream()
            .filter(u -> !u.getId().equals(this.id))
            .findFirst()
            .orElseThrow(PartnerNotFoundException::new);
    }

    @Getter
    public enum Role {

        GROOM("신랑"),
        BRIDE("신부"),
        USER("매칭안된 유저"),
        ADMIN("관리자"),
        TOGETHER("함께");

        private final String message;

        Role(String message) {
            this.message = message;
        }
    }
}
