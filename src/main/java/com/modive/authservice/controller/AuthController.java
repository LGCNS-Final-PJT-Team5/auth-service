package com.modive.authservice.controller;

import com.modive.authservice.domain.Admin;
import com.modive.authservice.dto.request.*;
import com.modive.authservice.dto.response.ApiResponse;
import com.modive.authservice.dto.response.SignUpSuccessResponse;
import com.modive.authservice.dto.response.TokenRefreshResponse;
import com.modive.authservice.exception.InvalidTokenException;
import com.modive.authservice.exception.SignupRequiredException;
import com.modive.authservice.exception.TokenRefreshException;
import com.modive.authservice.jwt.JwtTokenProvider;
import com.modive.authservice.repository.AdminRepository;
import com.modive.authservice.service.AdminDetailsService;
import com.modive.authservice.service.KakaoSocialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final KakaoSocialService kakaoSocialService;
    private final AdminDetailsService adminDetailsService;

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Value("${spring.oauth.kakao.client-id}")
    private String REST_API_KEY;

    @Value("${spring.oauth.kakao.redirect-uri}")
    private String REST_API_URI;

    @PostMapping("/test")
    public ApiResponse<String> test(
            @RequestBody final AccessTokenRequest request
    ) {
        String token = kakaoSocialService.testKakaoToken(request.getAccessToken());
        return new ApiResponse<>(HttpStatus.OK, token);
    }

    @GetMapping("/login")
    public ApiResponse<String> loginPath() {
        String url =  "https://kauth.kakao.com/oauth/authorize?client_id=" + REST_API_KEY + "&response_type=code&redirect_uri=" + REST_API_URI;
        return new ApiResponse<>(HttpStatus.OK, url);
    }

    @PostMapping("/kakao-login")
    public ApiResponse<SignUpSuccessResponse> signUp(
            @RequestBody final AccessTokenRequest request
    ) {
        try {
            SignUpSuccessResponse response = kakaoSocialService.kakaoSignUp(request.getAccessToken());
            return new ApiResponse<>(HttpStatus.OK, response);
        } catch (SignupRequiredException e) {
            return new ApiResponse<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/register")
    public ApiResponse<SignUpSuccessResponse> register(
            @RequestBody final KakaoRegisterRequest request
    ) {
        SignUpSuccessResponse response = kakaoSocialService.createKakaoUser(request);
        return new ApiResponse<>(HttpStatus.OK, response);
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse tokenRefreshResponse = kakaoSocialService.refreshToken(request);
        return new ApiResponse<>(HttpStatus.OK, tokenRefreshResponse);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestParam Long userId) {
        kakaoSocialService.revokeAllUserTokens(userId);
        return new ApiResponse<>(HttpStatus.OK);
    }

    @PostMapping("/admin/login")
    public ApiResponse<?> login(
            @RequestBody LoginRequest request)
    {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getId(), request.getPw())
        );
        return new ApiResponse<>(HttpStatus.OK, adminDetailsService.login(authentication));
    }

    @PostMapping("/admin/register")
    public ApiResponse<?> register(
            @RequestBody RegisterRequest request
    ) {
        if (adminRepository.existsById(request.getId())) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST,"이미 존재하는 ID입니다.");
        }

        Admin newAdmin = Admin.builder()
                .id(request.getId())
                .pw(passwordEncoder.encode(request.getPw())) // 반드시 암호화
                .nickname(request.getNickname())
                .build();

        adminRepository.save(newAdmin);

        return new ApiResponse<>(HttpStatus.OK,"관리자 추가에 성곻했습니다.");
    }

    @PostMapping("/admin/refresh")
    public ApiResponse<TokenRefreshResponse> refreshAdminToken(@RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse tokenRefreshResponse = adminDetailsService.refreshToken(request);
        return new ApiResponse<>(HttpStatus.OK, tokenRefreshResponse);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ApiResponse<String> handleTokenRefreshException(TokenRefreshException ex) {
        return new ApiResponse<>(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ApiResponse<String> handleInvalidTokenException(InvalidTokenException ex) {
        return new ApiResponse<>(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
}
