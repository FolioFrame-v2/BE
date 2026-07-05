package com.folioframe.domain.company.dto.request;

import com.folioframe.domain.company.enums.Industry;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CompanyProfileReqDTO {

    @NotBlank(message = "기업명은 필수 입력값입니다.")
    private String companyName;

    @NotNull(message = "분야(Industry)는 필수 입력값입니다.")
    private Industry industry;

    private String websiteUrl;

    private String companyIntro;

    @NotNull(message = "지역 ID는 필수 입력값입니다.")
    private Long regionId;

    private String employeeSize;

    @NotBlank(message = "담당자 이름은 필수 입력값입니다.")
    private String contactName;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String contactEmail;

    private String contactPhone;

    private List<Long> techStackIds;
}
