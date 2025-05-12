package com.modive.authservice.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.modive.authservice.domain.KakaoAccount;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserResponse(
        Long id,
        KakaoAccount kakaoAccount
) {
}