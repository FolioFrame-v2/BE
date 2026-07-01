package com.folioframe.global.auth;

import com.folioframe.global.auth.exception.AuthException;
import com.folioframe.global.auth.exception.code.AuthErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;

    public JwtUtil(
            @Value("${jwt.token.secret-key}") String secret,
            @Value("${jwt.token.expiration.access}") Long accessExpiration,
            @Value("${jwt.token.expiration.refresh}") Long refreshExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofMillis(accessExpiration);
        this.refreshExpiration = Duration.ofMillis(refreshExpiration);
    }

    public String createAccessToken(String loginId, String memberType) {
        return createToken(loginId, memberType, accessExpiration);
    }

    public String createRefreshToken(String loginId, String memberType) {
        return createToken(loginId, memberType, refreshExpiration);
    }

    private String createToken(String loginId, String memberType, Duration expiration) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(secretKey);

        if (loginId != null) {
            builder.subject(loginId);
            builder.claim("role", memberType);
        }
        return builder.compact();
    }

    public String getUserId(String token) {
        try {
            return getClaims(token).getPayload().getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            log.warn("유효하지 않은 토큰: {}", e.getMessage());
            return false;
        }
    }

    public void validateToken(String token) {
        try {
            getClaims(token);
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private Jws<Claims> getClaims(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }

    public Instant getExpiryDate(String token) {
        return getClaims(token).getPayload().getExpiration().toInstant();
    }

    public Long getAccessTokenExpirationMillis() {
        return this.accessExpiration.toMillis();
    }
}