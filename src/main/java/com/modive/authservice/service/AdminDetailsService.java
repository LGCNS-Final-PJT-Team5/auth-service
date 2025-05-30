package com.modive.authservice.service;

import com.modive.authservice.domain.Admin;
import com.modive.authservice.domain.AdminDetails;
import com.modive.authservice.domain.RefreshToken;
import com.modive.authservice.dto.request.LoginRequest;
import com.modive.authservice.dto.request.TokenRefreshRequest;
import com.modive.authservice.dto.response.SignUpSuccessResponse;
import com.modive.authservice.dto.response.TokenRefreshResponse;
import com.modive.authservice.exception.InvalidTokenException;
import com.modive.authservice.exception.TokenRefreshException;
import com.modive.authservice.jwt.JwtTokenProvider;
import com.modive.authservice.jwt.JwtValidationType;
import com.modive.authservice.repository.AdminRepository;
import com.modive.authservice.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {

    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 14 * 24 * 60 * 60 * 1000L;
    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public SignUpSuccessResponse login(Authentication authentication) {
        AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
        String id = adminDetails.getAdminId();

        String accessToken = jwtTokenProvider.generateTokenForAdmin(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshTokenForAdmin(authentication);

        refreshTokenRepository.findByUserId(id)
                .ifPresent(token -> refreshTokenRepository.delete(token));

        RefreshToken refreshTokenEntity = RefreshToken.create(refreshToken, id, REFRESH_TOKEN_EXPIRATION_TIME);
        refreshTokenRepository.save(refreshTokenEntity);

        return SignUpSuccessResponse.of(accessToken, refreshToken);
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
                    String adminId = refreshToken.getUserId();
                    String newAccessToken = jwtTokenProvider.generateAccessTokenFromAdminId(adminId);

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

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("관리자를 찾을 수 없습니다: " + id));

        return new AdminDetails(admin);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    @Transactional
    public void revokeAllUserTokens(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}