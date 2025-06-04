package com.wedvice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "matched_user_id", nullable = true)
    private Long matchedUserId;

    @Column(nullable = true)  // ✅ 메모 필드 (nullable)
    private String memo;

    @Column(nullable = true)
    private String refreshToken;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "groom")
    private List<Couple> groomCouples;

    @OneToMany(mappedBy = "bride")
    private List<Couple> brideCouples;

    public void updateRefreshToken(String newRefreshToken) {
        //  토큰 저장.
        this.refreshToken = refreshToken;
    }
}
