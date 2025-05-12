package com.modive.authservice.dto.request;

public class RegisterRequest {
    private String id;
    private String pw;
    private String nickname;

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }

    public String getNickname() {
        return nickname;
    }
}
