package com.wedvice.user.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.wedvice.common.BaseTimeEntity;
import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.exception.NotMatchedYetException;
import com.wedvice.couple.exception.PartnerNotFoundException;
import com.wedvice.user.entity.UserConfig.Color;
import com.wedvice.user.exception.InvalidRoleForWeddingException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(length = 2)
    private String nickname;

    private String profileImageUrl;

    @Column(nullable = true, length = 18)
    private String memo;

    @Column(nullable = true)
    private String refreshToken;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = true)
    private Role role;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "couple_id", nullable = true)
    private Couple couple;

    @OneToOne(mappedBy = "user", fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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

    // 테스트용 정적 팩토리 메서드 (엔티티 내부 유효성 검사 우회)
    public static User createForTest(String oauthId, String provider, String nickname,
        String memo) {
        return User.builder()
            .oauthId(oauthId)
            .provider(provider)
            .nickname(nickname)
            .memo(memo)
            .role(Role.USER) // 기본 역할 유저
            .build();
    }

    // 테스트용 정적 팩토리 메서드 (ID 포함)
    public static User createForTestWithId(Long id, String oauthId, String provider,
        String nickname, String memo) {
        User user = User.builder()
            .oauthId(oauthId)
            .provider(provider)
            .nickname(nickname)
            .memo(memo)
            .role(Role.USER)
            .build();
        user.id = id; // ID 직접 설정
        return user;
    }

    // 연관관계 편의 메서드
    public void matchCouple(Couple couple) {
        this.couple = couple;
        if (couple != null) {
            couple.getUsers().add(this);
        }
    }

    public void assignUserConfig(UserConfig config) {
        this.userConfig = config;
        config.setUser(this);
    }

    // 업데이트 메서드
    public void updateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty() || nickname.length() > 2) {
            throw new IllegalArgumentException("닉네임은 1자 이상 2자 이하여야 합니다.");
        }
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateMemo(String memo) {
        if (memo != null && memo.length() > 18) {
            throw new IllegalArgumentException("메모는 18자를 초과할 수 없습니다.");
        }
        this.memo = memo;
    }

    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateRole(User.Role role) {
        this.role = role;
    }

    public void addUserConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
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

    public Color provideThatColor(User.Role role) {
        if (!role.isMarriageRole()) {
            throw new InvalidRoleForWeddingException();
        }

        if (userConfig == null) {
            return Color.NOT_IMPLEMENT;
        }
        if (this.role == role) {
            return userConfig.getMyColor();
        } else if (role == Role.TOGETHER) {
            return userConfig.getOurColor();
        }
        return userConfig.getPartnerColor();
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

        public boolean isMarriageRole() {
            return this == GROOM || this == BRIDE || this == TOGETHER;
        }
    }
}
