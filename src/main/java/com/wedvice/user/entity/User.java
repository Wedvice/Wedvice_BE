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
    @Column(name = "role", nullable = false)
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

//    코드 입력(couple외래키가 있냐 없냐) -> 닉네임(nickname) -> 성별(role) ->
//    입력 다 하면 상대방 입력 대기 뻉글뻉글 -> 홈 화면에 언제 보내줄꺼냐?
//    (user.couple.users(!=id).getnickname,role !=null


    @Getter
    public static enum Role {

        GROOM("신랑"),
        BRIDE("신부");

        private final String message;

        Role(String message) {
            this.message=message;
        }
    }
}
