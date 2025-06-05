package com.modive.authservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1024)
    private String token;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public RefreshToken(String token, String userId, LocalDateTime expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.createdAt = LocalDateTime.now();
    }

    public RefreshToken() {};

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }

    public static RefreshToken create(String token, String userId, long expiryTimeInMillis) {
        LocalDateTime expiryDate = LocalDateTime.now().plusNanos(expiryTimeInMillis * 1_000_000);
        return RefreshToken.builder()
                .token(token)
                .userId(userId)
                .expiryDate(expiryDate)
                .build();
    }
}