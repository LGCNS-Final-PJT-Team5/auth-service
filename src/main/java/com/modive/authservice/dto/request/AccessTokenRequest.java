package com.modive.authservice.dto.request;

import lombok.Data;

@Data
public class AccessTokenRequest {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}