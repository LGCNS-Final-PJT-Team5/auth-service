package com.modive.authservice.dto.response;

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

    public static SignUpSuccessResponse of(String accessToken, String refreshToken) {
        return SignUpSuccessResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}