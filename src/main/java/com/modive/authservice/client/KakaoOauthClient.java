package com.modive.authservice.client;

import com.modive.authservice.config.FeignConfig;
import com.modive.authservice.response.KakaoTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "kakaoOauthClient",
        url = "https://kauth.kakao.com",
        configuration = FeignConfig.class)
public interface KakaoOauthClient {
    @PostMapping(value = "/oauth/token")
    KakaoTokenResponse getToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code,
            @RequestParam("client_secret") String clientSecret);
}