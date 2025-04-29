package com.modive.authservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class SignUpSuccessResponse {

    private String accessToken;
    private String refreshToken;

    public SignUpSuccessResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static SignUpSuccessResponse of(String token) {
        return SignUpSuccessResponse.builder()
                .accessToken(token)
                .refreshToken("refresh_token")
                .build();
    }
}