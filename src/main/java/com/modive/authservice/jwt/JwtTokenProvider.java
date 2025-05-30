package com.modive.authservice.jwt;

import com.modive.authservice.domain.AdminDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String USER_ID = "userId";
    private static final Long TOKEN_EXPIRATION_TIME = 60 * 60 * 1000L;
    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 14 * 24 * 60 * 60 * 1000L;

    @Value("${spring.jwt.secret}")
    private String JWT_SECRET;

    public String generateToken(Authentication authentication) {
        final Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(authentication.getPrincipal().toString()) // memberId를 subject로 유지
                .claim("userId", authentication.getPrincipal().toString())
                .claim("role", "USER") // gateway-service와 일치하도록 role 값 설정
                .setIssuer("http://localhost") // gateway-service와 일치하는 issuer 설정
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TOKEN_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        final Date now = new Date();
        final String tokenId = UUID.randomUUID().toString();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(authentication.getPrincipal().toString())
                .claim("userId", authentication.getPrincipal().toString())
                .claim("role", "USER")
                .setId(tokenId) // jti 클레임 설정 - 토큰 고유 식별자
                .setIssuer("http://localhost")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // UUID 타입으로 변경
    public String generateAccessTokenFromUserId(UUID userId) {
        final Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(userId.toString()) // UUID를 문자열로 변환
                .claim("userId", userId.toString()) // UUID를 문자열로 저장
                .claim("role", "USER")
                .setIssuer("http://localhost")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TOKEN_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public JwtValidationType validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return JwtValidationType.EMPTY_JWT;
        }

        try {
            final Claims claims = getBody(token);
            return JwtValidationType.VALID_JWT;
        } catch (MalformedJwtException ex) {
            return JwtValidationType.INVALID_JWT_TOKEN;
        } catch (ExpiredJwtException ex) {
            return JwtValidationType.EXPIRED_JWT_TOKEN;
        } catch (UnsupportedJwtException ex) {
            return JwtValidationType.UNSUPPORTED_JWT_TOKEN;
        } catch (IllegalArgumentException ex) {
            return JwtValidationType.EMPTY_JWT;
        }
    }

    private Claims getBody(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // UUID 타입으로 변경
    public UUID getUserFromJwt(String token) {
        Claims claims = getBody(token);
        String userIdStr = claims.get(USER_ID).toString();
        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            // 기존 Long 값이 있을 수 있으므로 예외 처리
            throw new JwtException("Invalid UUID format in token: " + userIdStr, e);
        }
    }

    public String generateTokenForAdmin(Authentication authentication) {
        AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
        UUID adminId = adminDetails.getAdminId(); // Long -> UUID로 변경 필요

        final Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(adminId.toString()) // UUID를 문자열로 변환
                .claim("userId", adminId.toString()) // UUID를 문자열로 저장
                .claim("role", "ADMIN")
                .setIssuer("http://localhost")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TOKEN_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshTokenForAdmin(Authentication authentication) {
        final Date now = new Date();
        final String tokenId = UUID.randomUUID().toString();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(authentication.getPrincipal().toString())
                .claim("userId", authentication.getPrincipal().toString())
                .claim("role", "Admin")
                .setId(tokenId) // jti 클레임 설정 - 토큰 고유 식별자
                .setIssuer("http://localhost")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // UUID 타입으로 변경
    public String generateAccessTokenFromAdminId(UUID adminId) {
        final Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(adminId.toString()) // UUID를 문자열로 변환
                .claim("userId", adminId.toString()) // UUID를 문자열로 저장
                .claim("role", "ADMIN")
                .setIssuer("http://localhost")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TOKEN_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}