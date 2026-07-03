package com.folioframe.global.auth;

import com.folioframe.domain.token.entity.BlacklistedToken;
import com.folioframe.domain.token.repository.BlacklistedTokenRepository;
import com.folioframe.domain.token.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Refresh-Token");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isValid(token)) {
                Instant expiryDate = jwtUtil.getExpiryDate(token);
                if (!blacklistedTokenRepository.existsByToken(token)) {
                    blacklistedTokenRepository.save(new BlacklistedToken(token, expiryDate));
                }
            }
        }
        if (refreshToken != null) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
    }
}