package com.folioframe.domain.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CertificateReqDTO(

        @NotBlank(message = "자격증명은 필수입니다.")
        @Size(max = 200, message = "자격증명은 200자를 초과할 수 없습니다.")
        String name,

        @Size(max = 100, message = "발급 기관은 100자를 초과할 수 없습니다.")
        String issuer,

        LocalDate issuedAt,

        LocalDate expiresAt,

        @Size(max = 100, message = "자격증 번호는 100자를 초과할 수 없습니다.")
        String credentialId
) {}
