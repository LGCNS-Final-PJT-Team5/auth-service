package com.modive.authservice.dto.request;


import lombok.Data;

@Data
public class KakaoRegisterRequest {
    private String accessToken;
    private String nickname;
    private String interest;
    private String carNumber;
    private Long drivingExperience;
    private String fcmToken;
}