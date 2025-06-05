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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminDetailsServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AdminDetailsService adminDetailsService;

    public AdminDetailsServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRefreshTokenSuccess() {
        String validRefreshToken = "validRefreshToken";
        String userId = "testUserId";
        String newAccessToken = "newAccessToken";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(validRefreshToken);
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        when(jwtTokenProvider.validateToken(validRefreshToken)).thenReturn(JwtValidationType.VALID_JWT);
        when(refreshTokenRepository.findByToken(validRefreshToken)).thenReturn(Optional.of(refreshToken));
        when(jwtTokenProvider.generateAccessTokenFromAdminId(userId)).thenReturn(newAccessToken);

        TokenRefreshRequest request = new TokenRefreshRequest(validRefreshToken);
        TokenRefreshResponse response = adminDetailsService.refreshToken(request);

        assertNotNull(response);
        assertEquals(newAccessToken, response.accessToken());
        assertEquals(validRefreshToken, response.refreshToken());

        verify(refreshTokenRepository, times(1)).findByToken(validRefreshToken);
        verify(jwtTokenProvider, times(1)).validateToken(validRefreshToken);
        verify(jwtTokenProvider, times(1)).generateAccessTokenFromAdminId(userId);
    }

    @Test
    void testRefreshTokenInvalidToken() {
        String invalidRefreshToken = "invalidRefreshToken";

        when(jwtTokenProvider.validateToken(invalidRefreshToken)).thenReturn(JwtValidationType.INVALID_JWT_SIGNATURE);

        TokenRefreshRequest request = new TokenRefreshRequest(invalidRefreshToken);

        assertThrows(InvalidTokenException.class, () -> adminDetailsService.refreshToken(request));

        verify(jwtTokenProvider, times(1)).validateToken(invalidRefreshToken);
        verify(refreshTokenRepository, never()).findByToken(any());
        verify(jwtTokenProvider, never()).generateAccessTokenFromAdminId(any());
    }

    @Test
    void testRefreshTokenNotFound() {
        String nonExistentRefreshToken = "nonExistentToken";

        when(jwtTokenProvider.validateToken(nonExistentRefreshToken)).thenReturn(JwtValidationType.VALID_JWT);
        when(refreshTokenRepository.findByToken(nonExistentRefreshToken)).thenReturn(Optional.empty());

        TokenRefreshRequest request = new TokenRefreshRequest(nonExistentRefreshToken);

        TokenRefreshException exception = assertThrows(TokenRefreshException.class, () -> adminDetailsService.refreshToken(request));
        assertTrue(exception.getMessage().contains("Refresh token not found in database"));

        verify(jwtTokenProvider, times(1)).validateToken(nonExistentRefreshToken);
        verify(refreshTokenRepository, times(1)).findByToken(nonExistentRefreshToken);
        verify(jwtTokenProvider, never()).generateAccessTokenFromAdminId(any());
    }

    @Test
    void testRefreshTokenExpiredToken() {
        String expiredRefreshToken = "expiredRefreshToken";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(expiredRefreshToken);
        refreshToken.setUserId("testUserId");
        refreshToken.setExpiryDate(LocalDateTime.now().minusDays(1));

        when(jwtTokenProvider.validateToken(expiredRefreshToken)).thenReturn(JwtValidationType.VALID_JWT);
        when(refreshTokenRepository.findByToken(expiredRefreshToken)).thenReturn(Optional.of(refreshToken));

        TokenRefreshRequest request = new TokenRefreshRequest(expiredRefreshToken);

        TokenRefreshException exception = assertThrows(TokenRefreshException.class, () -> adminDetailsService.refreshToken(request));
        assertTrue(exception.getMessage().contains("Refresh token was expired"));

        verify(jwtTokenProvider, times(1)).validateToken(expiredRefreshToken);
        verify(refreshTokenRepository, times(1)).findByToken(expiredRefreshToken);
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
        verify(jwtTokenProvider, never()).generateAccessTokenFromAdminId(any());
    }
}