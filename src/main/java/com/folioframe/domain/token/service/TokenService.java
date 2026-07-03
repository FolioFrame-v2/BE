package com.folioframe.domain.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

// refresh token / access token 블랙리스트를 Redis에 TTL과 함께 저장 — 토큰 자연 만료 시 자동으로 정리됨
@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh-token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final StringRedisTemplate redisTemplate;

    public void saveRefreshToken(String loginId, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + loginId, refreshToken, ttl);
    }

    public Optional<String> getRefreshToken(String loginId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + loginId));
    }

    public void deleteRefreshToken(String loginId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + loginId);
    }

    public void blacklistAccessToken(String accessToken, Duration ttl) {
        if (ttl.isNegative() || ttl.isZero()) {
            return; // 이미 만료된 토큰은 굳이 블랙리스트에 넣을 필요 없음
        }
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + accessToken, "1", ttl);
    }

    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken));
    }
}
