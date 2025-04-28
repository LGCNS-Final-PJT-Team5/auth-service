package com.modive.authservice.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessMessage {
    // 성공 메시지 상수
    public static final String SIGNUP_SUCCESS = "회원가입에 성공했습니다.";
    public static final String LOGIN_SUCCESS = "로그인에 성공했습니다.";
    public static final String LOGOUT_SUCCESS = "로그아웃에 성공했습니다.";
    public static final String DELETE_SUCCESS = "삭제에 성공했습니다.";
    public static final String UPDATE_SUCCESS = "업데이트에 성공했습니다.";
    
    private int status;
    private String message;

    private SuccessMessage(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public static SuccessMessage of(HttpStatus status, String message) {
        return new SuccessMessage(status, message);
    }

    public static SuccessMessage ok(String message) {
        return new SuccessMessage(HttpStatus.OK, message);
    }

    public static SuccessMessage created(String message) {
        return new SuccessMessage(HttpStatus.CREATED, message);
    }
}