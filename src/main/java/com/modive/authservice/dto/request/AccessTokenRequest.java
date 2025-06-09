package com.modive.authservice.dto.request;

import lombok.Data;

@Data
public class AccessTokenRequest {
    private String accessToken;
    private String fcmToken;
}