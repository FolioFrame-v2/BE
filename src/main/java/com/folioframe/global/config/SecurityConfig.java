package com.folioframe.global.config;

import com.folioframe.domain.token.service.TokenService;
import com.folioframe.global.auth.*;
import com.folioframe.global.auth.exception.code.AuthErrorCode;
import com.folioframe.global.auth.exception.code.AuthSuccessCode;
import com.folioframe.global.util.FilterResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final JwtLogoutHandler jwtLogoutHandler;
    private final TokenService tokenService;
    private final FilterResponseUtils filterResponseUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/login/oauth2/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health"
                        ).permitAll()
                        // 비로그인 사용자도 조회 가능한 공개 API — 순서상 더 구체적인 경로를 와일드카드 경로보다 먼저 선언
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/portfolios/public",
                                "/api/v1/portfolios/slug/*",
                                "/api/v1/portfolios/*",
                                "/api/v1/activities",
                                "/api/v1/portfolio-templates",
                                "/api/v1/portfolio-templates/*"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/activities/*/views").permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(a -> a.baseUri("/api/auth/login"))
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )

                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, customUserDetailsService, tokenService, filterResponseUtils),
                        org.springframework.security.web.authentication.logout.LogoutFilter.class)

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler(customLogoutSuccessHandler())
                )

                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) ->
                        filterResponseUtils.sendErrorResponse(res, AuthErrorCode.EMPTY_AUTHENTICATION)));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private LogoutSuccessHandler customLogoutSuccessHandler() {
        return (request, response, auth) -> {
            response.setContentType("application/json;charset=UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(
                    com.folioframe.global.apiPayload.ApiResponse.onSuccess(AuthSuccessCode.LOGOUT_SUCCESS, null)));
        };
    }
}