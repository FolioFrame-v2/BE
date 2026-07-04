package com.folioframe.domain.company.dto.request;

import com.folioframe.domain.company.enums.Industry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompanyProfileReqDTO {

    @NotBlank(message = "기업명은 필수 입력값입니다.")
    private String companyName;

    @NotBlank(message = "사업자 등록번호는 필수 입력값입니다.")
    private String businessNumber;

    @NotNull(message = "분야(Industry)는 필수 입력값입니다.")
    private Industry industry;

    private String websiteUrl;

    private String logoUrl;

    private String companyIntro;

    @NotNull(message = "지역 ID는 필수 입력값입니다.")
    private Long regionId;

    private String employeeSize;
}