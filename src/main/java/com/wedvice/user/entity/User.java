package com.wedvice.user.entity;

import com.wedvice.couple.entity.Couple;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String oauthId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = true)
    private String nickname;

    private String profileImageUrl;

    @Column(nullable = true)  // ✅ 메모 필드 (nullable)
    private String memo;

    @Column(nullable = true)
    private String refreshToken;

    @Email
    private String email;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = true)
    private Role role;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id", nullable = true)
    private Couple couple;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserConfig userConfig;

    public void updateRefreshToken(String newRefreshToken) {
        //  토큰 저장.
        this.refreshToken = newRefreshToken;
    }


    @Getter
    public static enum Role {

        GROOM("신랑"),
        BRIDE("신부");

        private final String message;

        Role(String message) {
            this.message = message;
        }
    }
}
