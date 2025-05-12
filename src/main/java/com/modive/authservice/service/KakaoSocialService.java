package com.modive.authservice.service;

import com.modive.authservice.client.KakaoApiClient;
import com.modive.authservice.client.KakaoOauthClient;
import com.modive.authservice.domain.RefreshToken;
import com.modive.authservice.domain.User;
import com.modive.authservice.dto.request.TokenRefreshRequest;
import com.modive.authservice.dto.response.TokenRefreshResponse;
import com.modive.authservice.exception.InvalidTokenException;
import com.modive.authservice.exception.TokenRefreshException;
import com.modive.authservice.jwt.JwtTokenProvider;
import com.modive.authservice.jwt.JwtValidationType;
import com.modive.authservice.jwt.UserAuthentication;
import com.modive.authservice.properties.KakaoProperties;
import com.modive.authservice.repository.RefreshTokenRepository;
import com.modive.authservice.repository.UserRepository;
import com.modive.authservice.dto.response.KakaoTokenResponse;
import com.modive.authservice.dto.response.KakaoUserResponse;
import com.modive.authservice.dto.response.SignUpSuccessResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoSocialService {

    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 14 * 24 * 60 * 60 * 1000L;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final KakaoApiClient kakaoApiClient;
    private final JwtTokenProvider jwtTokenProvider;

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

        Optional<User> user = findKakaoUser(userResponse.id());

        Long id = user.map(User::getUserId)
                .orElse(-1L);

        if (id == -1L) {
            id = createKakaoUser(userResponse);
        }

        UserAuthentication userAuthentication = new UserAuthentication(id, null, null);

        String jwtAccessToken = jwtTokenProvider.generateToken(userAuthentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userAuthentication);

        refreshTokenRepository.findByUserId(id)
                .ifPresent(token -> refreshTokenRepository.delete(token));

        RefreshToken refreshTokenEntity = RefreshToken.create(refreshToken, id, REFRESH_TOKEN_EXPIRATION_TIME);
        refreshTokenRepository.save(refreshTokenEntity);

        return SignUpSuccessResponse.of(jwtAccessToken, refreshToken);
    }

    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.refreshToken();

        // 리프레시 토큰 유효성 검증
        JwtValidationType validationType = jwtTokenProvider.validateToken(requestRefreshToken);
        if (validationType != JwtValidationType.VALID_JWT) {
            throw new InvalidTokenException("Invalid refresh token: " + validationType);
        }

        // DB에서 리프레시 토큰 조회
        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyRefreshTokenExpiration)
                .map(refreshToken -> {
                    // 새 액세스 토큰 생성
                    Long userId = refreshToken.getUserId();
                    String newAccessToken = jwtTokenProvider.generateAccessTokenFromUserId(userId);

                    return new TokenRefreshResponse(newAccessToken, requestRefreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token not found in database"));
    }

    private RefreshToken verifyRefreshTokenExpiration(RefreshToken refreshToken) {
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenRefreshException(refreshToken.getToken(), "Refresh token was expired");
        }

        return refreshToken;
    }

    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public Long createKakaoUser(final KakaoUserResponse userResponse) {
        User user = User.of(
                userResponse.kakaoAccount().profile().nickname(),
                userResponse.kakaoAccount().profile().accountEmail(),
                String.valueOf(userResponse.id()),
                "kakao"
        );
        return userRepository.save(user).getUserId();
    }

    public Optional<User> findKakaoUser(final Long socialId) {
        return userRepository.findBySocialIdAndSocialType(String.valueOf(socialId), "kakao");
    }
}