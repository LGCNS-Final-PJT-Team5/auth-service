package com.modive.authservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpSuccessResponse {
    
    private Long userId;
    private String email;
    private String nickname;
    private String name;
    private String accessToken;
    private String refreshToken;
    
    // JWT 토큰 정보만 포함한 생성자
    public SignUpSuccessResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    
    // 토큰 하나만 받는 팩토리 메서드 (accessToken만 설정)
    public static SignUpSuccessResponse of(String token) {
        return SignUpSuccessResponse.builder()
                .accessToken(token)
                .build();
    }
}