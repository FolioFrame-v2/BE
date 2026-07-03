package com.folioframe.domain.company.dto.request;

import com.folioframe.domain.company.enums.VerificationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompanyVerificationReqDTO {
    private VerificationStatus verificationStatus;
}
