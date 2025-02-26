package com.wedvice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "couples")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Couple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groom_id", nullable = false)
    private User groom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bride_id", nullable = false)
    private User bride;

    @Column(name = "wedding_date", nullable = true)  // ✅ 결혼 날짜 nullable
    private LocalDate weddingDate;

    @CreationTimestamp
    private LocalDate createdAt;
}
