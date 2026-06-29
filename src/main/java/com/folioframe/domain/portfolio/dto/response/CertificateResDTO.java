package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioCertificate;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CertificateResDTO(
        Long id,
        Long portfolioId,
        String name,
        String issuer,
        LocalDate issuedAt,
        LocalDate expiresAt,
        String credentialId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CertificateResDTO from(PortfolioCertificate certificate) {
        return new CertificateResDTO(
                certificate.getId(),
                certificate.getPortfolio().getId(),
                certificate.getName(),
                certificate.getIssuer(),
                certificate.getIssuedAt(),
                certificate.getExpiresAt(),
                certificate.getCredentialId(),
                certificate.getCreatedAt(),
                certificate.getUpdatedAt()
        );
    }
}
