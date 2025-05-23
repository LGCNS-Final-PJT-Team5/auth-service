package com.modive.authservice.dto.request;


public class KakaoRegisterRequest {
    private String accessToken;
    private String nickname;
    private String interest;
    private String carNumber;
    private Long drivingExperience;

    public String getAccessToken() {
        return accessToken;
    }

    public String getNickname() { return nickname; }

    public String getInterest() { return interest; }

    public String getCarNumber() { return carNumber; }

    public Long getDrivingExperience() { return drivingExperience; }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public void setInterest(String interest) { this.interest = interest; }

    public void setCarNumber(String carNumber) { this.carNumber = carNumber; }

    public void setDrivingExperience(Long drivingExperience) { this.drivingExperience = drivingExperience; }
}