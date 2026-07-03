package com.folioframe.domain.company.service;

import com.folioframe.domain.company.dto.response.CompanyProfileResDTO;
import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.company.enums.VerificationStatus;
import com.folioframe.domain.company.exception.CompanyException;
import com.folioframe.domain.company.exception.code.CompanyErrorCode;
import com.folioframe.domain.company.repository.CompanyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyProfileRepository companyProfileRepository;

    @Transactional
    public CompanyProfileResDTO updateVerificationStatus(Long companyProfileId, VerificationStatus verificationStatus) {
        CompanyProfile companyProfile = companyProfileRepository.findById(companyProfileId)
                .orElseThrow(() -> new CompanyException(CompanyErrorCode.COMPANY_PROFILE_NOT_FOUND));

        companyProfile.updateVerificationStatus(verificationStatus);
        return CompanyProfileResDTO.from(companyProfile);
    }
}
