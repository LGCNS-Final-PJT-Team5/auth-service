package com.modive.authservice.controller;

import com.modive.authservice.dto.AccessTokenRequest;
import com.modive.authservice.response.SignUpSuccessResponse;
import com.modive.authservice.service.KakaoSocialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.modive.authservice.response.SuccessResponse;
import com.modive.authservice.response.SuccessMessage;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoSocialService kakaoSocialService;

    @Value("${oauth.kakao.client-id}")
    private String REST_API_KEY;

    @Value("${oauth.kakao.redirect-uri}")
    private String REST_API_URI;

    @GetMapping("/login")
    public String loginPath() {
        return "https://kauth.kakao.com/oauth/authorize?client_id=" + REST_API_KEY + "&response_type=code&redirect_uri=" + REST_API_URI;
    }

    @PostMapping("/kakao-login")
    public SuccessResponse<SignUpSuccessResponse> signUp(
            @RequestBody final AccessTokenRequest request
    ) {
        return SuccessResponse.of(
                SuccessMessage.SIGNUP_SUCCESS,
                kakaoSocialService.kakaoSignUp(request.getAccessToken())
        );
    }
}
