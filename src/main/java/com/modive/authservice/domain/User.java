package com.modive.authservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String nickname;

    private String password;

    @Column(nullable = false)
    private String name;

    private String profileImage;

    private String socialId;  // 소셜 로그인 ID (카카오, 구글 등)

    private String socialType; // 소셜 로그인 유형 (KAKAO, GOOGLE 등)

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 사용자 정보 업데이트 메서드
    public void updateProfile(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
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
    public static User of(String nickname, String profileImage, String email, String socialId) {
        return User.builder()
                .nickname(nickname)
                .profileImage(profileImage)
                .email(email)
                .socialId(socialId)
                .socialType("KAKAO") // 기본값으로 카카오 설정
                .name(nickname) // 기본값으로 닉네임을 이름으로 사용
                .build();
    }

}