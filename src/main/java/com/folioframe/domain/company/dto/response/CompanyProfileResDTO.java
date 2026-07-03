package com.folioframe.domain.company.dto.response;

import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.company.enums.VerificationStatus;

public record CompanyProfileResDTO(
        Long id,
        Long memberId,
        String companyName,
        String businessNumber,
        VerificationStatus verificationStatus
) {
    public static CompanyProfileResDTO from(CompanyProfile profile) {
        return new CompanyProfileResDTO(
                profile.getId(),
                profile.getMember().getId(),
                profile.getCompanyName(),
                profile.getBusinessNumber(),
                profile.getVerificationStatus()
        );
    }
}
