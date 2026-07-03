package com.folioframe.global.auth;

import com.folioframe.domain.token.service.TokenService;
import com.folioframe.global.auth.exception.AuthException;
import com.folioframe.global.util.FilterResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenService tokenService;
    private final FilterResponseUtils filterResponseUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            try {
                // 블랙리스트 체크
                if (tokenService.isBlacklisted(token)) {
                    throw new AuthException(com.folioframe.global.auth.exception.code.AuthErrorCode.INVALID_TOKEN);
                }

                // 토큰 검증
                jwtUtil.validateToken(token);

                // 인증 처리
                String loginId = jwtUtil.getUserId(token);
                CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (AuthException e) {
                filterResponseUtils.sendErrorResponse(response, e.getCode());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}