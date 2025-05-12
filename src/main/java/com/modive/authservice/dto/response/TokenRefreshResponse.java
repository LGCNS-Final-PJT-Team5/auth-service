package com.modive.authservice.dto.response;

public record TokenRefreshResponse(String accessToken, String refreshToken) {
}
