package com.folioframe.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String MEMBER_ID_HEADER = "X-Member-Id";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FolioFrame API")
                        .description("FolioFrame 포트폴리오 서비스 API 문서")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(MEMBER_ID_HEADER))
                .components(new Components()
                        .addSecuritySchemes(MEMBER_ID_HEADER, new SecurityScheme()
                                .name(MEMBER_ID_HEADER)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("인증된 회원 ID (임시 인증 방식 — JWT 도입 전)")));
    }
}
