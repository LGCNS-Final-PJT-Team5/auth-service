package com.modive.authservice.response;

import com.modive.authservice.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long reward;
    private String nickname;
    private String name;
    private String email;
    private LocalDateTime birthdate;
    private LocalDateTime licenseDate;
    private boolean alarm;
    private String gender;
    private String phone;

    public UserResponse(User user) {
        this.reward = user.getReward();
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.email = user.getEmail();
        this.birthdate = user.getBirthdate();
        this.licenseDate = user.getLicenseDate();
        this.alarm = user.isAlarm();
        this.gender = user.getGender();
        this.phone = user.getPhone();
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .reward(user.getReward())
                .nickname(user.getNickname())
                .name(user.getName())
                .email(user.getEmail())
                .birthdate(user.getBirthdate())
                .licenseDate(user.getLicenseDate())
                .alarm(user.isAlarm())
                .gender(user.getGender())
                .phone(user.getPhone())
                .build();
    }
}
