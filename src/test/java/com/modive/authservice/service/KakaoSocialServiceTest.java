package com.modive.authservice.service;

import com.modive.authservice.domain.RefreshToken;
import com.modive.authservice.dto.request.TokenRefreshRequest;
import com.modive.authservice.dto.response.TokenRefreshResponse;
import com.modive.authservice.exception.InvalidTokenException;
import com.modive.authservice.exception.TokenRefreshException;
import com.modive.authservice.jwt.JwtTokenProvider;
import com.modive.authservice.jwt.JwtValidationType;
import com.modive.authservice.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class KakaoSocialServiceTest {

    @InjectMocks
    private KakaoSocialService kakaoSocialService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    KakaoSocialServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void refreshToken_shouldReturnNewAccessToken_whenRefreshTokenIsValid() {
        String validRefreshToken = "valid-token";
        String userId = "user-id";
        String newAccessToken = "new-access-token";

        TokenRefreshRequest request = new TokenRefreshRequest(validRefreshToken);
        RefreshToken storedRefreshToken = RefreshToken.create(validRefreshToken, userId, 14 * 24 * 60 * 60 * 1000L);

        when(jwtTokenProvider.validateToken(validRefreshToken)).thenReturn(JwtValidationType.VALID_JWT);
        when(refreshTokenRepository.findByToken(validRefreshToken)).thenReturn(Optional.of(storedRefreshToken));
        when(jwtTokenProvider.generateAccessTokenFromUserId(userId)).thenReturn(newAccessToken);

        TokenRefreshResponse response = kakaoSocialService.refreshToken(request);

        assertEquals(newAccessToken, response.accessToken());
        assertEquals(validRefreshToken, response.refreshToken());

        verify(jwtTokenProvider).validateToken(validRefreshToken);
        verify(refreshTokenRepository).findByToken(validRefreshToken);
        verify(jwtTokenProvider).generateAccessTokenFromUserId(userId);
    }

    @Test
    void refreshToken_shouldThrowInvalidTokenException_whenRefreshTokenIsInvalid() {
        String invalidRefreshToken = "invalid-token";

        TokenRefreshRequest request = new TokenRefreshRequest(invalidRefreshToken);

        when(jwtTokenProvider.validateToken(invalidRefreshToken)).thenReturn(JwtValidationType.INVALID_JWT_SIGNATURE);

        assertThrows(InvalidTokenException.class, () -> kakaoSocialService.refreshToken(request));

        verify(jwtTokenProvider).validateToken(invalidRefreshToken);
        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void refreshToken_shouldThrowTokenRefreshException_whenRefreshTokenNotFound() {
        String validRefreshToken = "valid-token";

        TokenRefreshRequest request = new TokenRefreshRequest(validRefreshToken);

        when(jwtTokenProvider.validateToken(validRefreshToken)).thenReturn(JwtValidationType.VALID_JWT);
        when(refreshTokenRepository.findByToken(validRefreshToken)).thenReturn(Optional.empty());

        assertThrows(TokenRefreshException.class, () -> kakaoSocialService.refreshToken(request));

        verify(jwtTokenProvider).validateToken(validRefreshToken);
        verify(refreshTokenRepository).findByToken(validRefreshToken);
    }

    @Test
    void refreshToken_shouldThrowTokenRefreshException_whenRefreshTokenIsExpired() {
        String validRefreshToken = "valid-token";
        String userId = "user-id";

        TokenRefreshRequest request = new TokenRefreshRequest(validRefreshToken);
        RefreshToken storedRefreshToken = mock(RefreshToken.class);

        when(jwtTokenProvider.validateToken(validRefreshToken)).thenReturn(JwtValidationType.VALID_JWT);
        when(refreshTokenRepository.findByToken(validRefreshToken)).thenReturn(Optional.of(storedRefreshToken));
        when(storedRefreshToken.isExpired()).thenReturn(true);

        assertThrows(TokenRefreshException.class, () -> kakaoSocialService.refreshToken(request));

        verify(jwtTokenProvider).validateToken(validRefreshToken);
        verify(refreshTokenRepository).findByToken(validRefreshToken);
        verify(refreshTokenRepository).delete(storedRefreshToken);
    }
}