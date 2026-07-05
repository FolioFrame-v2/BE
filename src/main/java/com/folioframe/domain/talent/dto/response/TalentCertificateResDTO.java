package com.folioframe.domain.talent.dto.response;

import com.folioframe.domain.talent.entity.TalentCertificate;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TalentCertificateResDTO(
        Long id,
        Long talentProfileId,
        String name,
        String issuer,
        LocalDate issuedAt,
        LocalDate expiresAt,
        String credentialId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TalentCertificateResDTO from(TalentCertificate certificate) {
        return new TalentCertificateResDTO(
                certificate.getId(),
                certificate.getTalentProfile().getId(),
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
