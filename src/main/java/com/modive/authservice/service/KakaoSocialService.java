package com.modive.authservice.service;

import com.modive.authservice.client.KakaoApiClient;
import com.modive.authservice.client.KakaoOauthClient;
import com.modive.authservice.domain.User;
import com.modive.authservice.jwt.JwtTokenProvider;
import com.modive.authservice.jwt.UserAuthentication;
import com.modive.authservice.properties.KakaoProperties;
import com.modive.authservice.repository.UserRepository;
import com.modive.authservice.response.KakaoTokenResponse;
import com.modive.authservice.response.KakaoUserResponse;
import com.modive.authservice.response.SignUpSuccessResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoSocialService {

    private final UserRepository userRepository;
    private final KakaoApiClient kakaoApiClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    private final KakaoOauthClient kakaoOauthClient;
    private final KakaoProperties kakaoProperties;

    @Transactional
    public KakaoTokenResponse getIdToken(String code) {
        return kakaoOauthClient.getToken(
                kakaoProperties.grantType(),
                kakaoProperties.clientId(),
                kakaoProperties.redirectUri(),
                code,
                kakaoProperties.clientSecret());
    }

    @Transactional
    public SignUpSuccessResponse kakaoSignUp(final String code) {
        KakaoTokenResponse response = getIdToken(code);

        String accessToken = response.getAccessToken();

        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + accessToken);

        System.out.println(userResponse);

        Optional<User> user = userService.findKakaoUser(userResponse.id());

        Long id = user.map(User::getUserId)
                .orElse(-1L);

        if (id == -1L) {
            id = userService.createKakaoUser(userResponse);
        }

        System.out.println(id);

        UserAuthentication userAuthentication = new UserAuthentication(id, null, null);

        return SignUpSuccessResponse.of(jwtTokenProvider.generateToken(userAuthentication));
    }
}