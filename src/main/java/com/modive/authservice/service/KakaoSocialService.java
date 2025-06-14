package com.modive.authservice.service;

import com.modive.authservice.client.KakaoApiClient;
import com.modive.authservice.client.KakaoOauthClient;
import com.modive.authservice.domain.Car;
import com.modive.authservice.domain.RefreshToken;
import com.modive.authservice.domain.User;
import com.modive.authservice.dto.request.KakaoRegisterRequest;
import com.modive.authservice.dto.request.TokenRefreshRequest;
import com.modive.authservice.dto.response.TokenRefreshResponse;
import com.modive.authservice.exception.InvalidTokenException;
import com.modive.authservice.exception.SignupRequiredException;
import com.modive.authservice.exception.TokenRefreshException;
import com.modive.authservice.jwt.JwtTokenProvider;
import com.modive.authservice.jwt.JwtValidationType;
import com.modive.authservice.jwt.UserAuthentication;
import com.modive.authservice.properties.KakaoProperties;
import com.modive.authservice.repository.CarRepository;
import com.modive.authservice.repository.RefreshTokenRepository;
import com.modive.authservice.repository.UserRepository;
import com.modive.authservice.dto.response.KakaoTokenResponse;
import com.modive.authservice.dto.response.KakaoUserResponse;
import com.modive.authservice.dto.response.SignUpSuccessResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoSocialService {

    private static final Logger logger = LoggerFactory.getLogger(KakaoSocialService.class);

    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 14 * 24 * 60 * 60 * 1000L;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final KakaoApiClient kakaoApiClient;
    private final JwtTokenProvider jwtTokenProvider;

    private final KakaoOauthClient kakaoOauthClient;
    private final KakaoProperties kakaoProperties;

    private final CarRepository carRepository;

    @Transactional
    public KakaoTokenResponse getIdToken(String code) {
        return kakaoOauthClient.getToken(
                kakaoProperties.grantType(),
                kakaoProperties.clientId(),
                kakaoProperties.redirectUri(),
                code,
                kakaoProperties.clientSecret());
    }

    public String testKakaoToken(final String code) {
        try {
            KakaoTokenResponse response = getIdToken(code);

            String accessToken = response.getAccessToken();

            KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + accessToken);

            return accessToken;
        } catch (Exception e) {
            logger.error("Error occurred while getting kakao token", e);
            return null;
        }
    }

    @Transactional
    public SignUpSuccessResponse kakaoSignUp(final String accessToken, final String fcmToken) {

        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + accessToken);
        log.info("[kakaoSignUp] Kakao 사용자 정보 수신 완료: id={}, email={}",
                userResponse.id(),
                userResponse.kakaoAccount() != null ? userResponse.kakaoAccount().email() : "null");

        User user = userRepository.findBySocialIdAndSocialType(String.valueOf(userResponse.id()), "kakao")
                .orElseThrow(SignupRequiredException::new);

        // FCM 토큰이 전달되었으면 업데이트
        if (fcmToken != null && !fcmToken.isBlank()) {
            user.setFcmToken(fcmToken);
        }

        String[] tokenSet = generateToken(user.getUserId());

        return SignUpSuccessResponse.of(tokenSet[0], tokenSet[1]);
    }

    private String[] generateToken(String id) {
        UserAuthentication userAuthentication = new UserAuthentication(id, null, null);

        String jwtAccessToken = jwtTokenProvider.generateToken(userAuthentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userAuthentication);

        refreshTokenRepository.findByUserId(id)
                .ifPresent(token -> refreshTokenRepository.delete(token));

        RefreshToken refreshTokenEntity = RefreshToken.create(refreshToken, id, REFRESH_TOKEN_EXPIRATION_TIME);
        refreshTokenRepository.save(refreshTokenEntity);
        return new String[]{jwtAccessToken, refreshToken};
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
                    String userId = refreshToken.getUserId();
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
    public void revokeAllUserTokens(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public SignUpSuccessResponse createKakaoUser(final KakaoRegisterRequest request) {

        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + request.getAccessToken());

        User user = User.of(
                userResponse.kakaoAccount().profile().nickname(),
                request.getNickname(),
                userResponse.kakaoAccount().email(),
                request.getInterest(),
                request.getDrivingExperience(),
                String.valueOf(userResponse.id()),
                "kakao"
        );

        // FCM 토큰 세팅
        user.setFcmToken(request.getFcmToken());

        String id = userRepository.save(user).getUserId();

        Car car = Car.of(user, request.getCarNumber());
        carRepository.save(car);

        String[] TokenSet = generateToken(id);

        return SignUpSuccessResponse.of(TokenSet[0], TokenSet[1]);

    }

    public Optional<User> findKakaoUser(final Long socialId) {
        return userRepository.findBySocialIdAndSocialType(String.valueOf(socialId), "kakao");
    }
}