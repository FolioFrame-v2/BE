package com.folioframe.domain.company.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyProfileSignupInfoResDTO {
    private String contactName;
    private String contactPhone;
    private String businessNumber;
}
