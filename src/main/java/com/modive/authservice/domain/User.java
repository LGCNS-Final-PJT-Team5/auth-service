package com.modive.authservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private Long reward;

    private String nickname;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private boolean alarm;

    private String interest;

    private Long drivingExperience;

    private String socialId;  // 소셜 로그인 ID (카카오, 구글 등)

    private String socialType; // 소셜 로그인 유형 (KAKAO, GOOGLE 등)

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDateTime;

    @LastModifiedDate
    private LocalDateTime updateDateTime;

    private boolean isActive;

    public Long getUserId() {
        return userId;
    }

    // 사용자 정보 업데이트 메서드
    public void updateProfile(String nickname, String profileImage) {
        this.nickname = nickname;
    }

    // 소셜 로그인 정보 업데이트
    public void updateSocialInfo(String socialId, String socialType) {
        this.socialId = socialId;
        this.socialType = socialType;
    }

    // 이메일 업데이트
    public void updateEmail(String email) {
        this.email = email;
    }

    // 정적 팩토리 메서드
    public static User of(String name, String nickname, String email, String interest, Long drivingExperience, String socialId, String socialType) {
        return User.builder()
                .reward(0L)
                .nickname(nickname)
                .name(name)
                .email(email)
                .interest(interest)
                .drivingExperience(drivingExperience)
                .alarm(false)
                .socialId(socialId)
                .socialType(socialType)
                .isActive(true)
                .build();
    }
}