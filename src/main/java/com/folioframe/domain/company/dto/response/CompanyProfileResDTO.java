package com.folioframe.domain.company.dto.response;

import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.company.enums.Industry;
import com.folioframe.domain.company.enums.VerificationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CompanyProfileResDTO {
    private Long companyProfileId;
    private String companyName;
    private String businessNumber;
    private Industry industry;
    private String websiteUrl;
    private String companyIntro;
    private String employeeSize;
    private Long regionId;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private List<TechstackResDTO> techStacks;
    private VerificationStatus verificationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
