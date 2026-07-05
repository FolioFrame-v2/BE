package com.folioframe.domain.talent.dto.request;

import com.folioframe.domain.common.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class TalentProfileUpdateReqDTO {

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotNull(message = "거주 지역을 선택해주세요.")
    private Long regionId;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String contactEmail;

    private String phoneNumber;
    private Integer age;
    private Gender gender;

    @NotBlank(message = "GitHub 링크는 필수 입력값입니다.")
    private String githubUrl;
    private String portfolioWebsite;

    @NotEmpty(message = "파트를 선택해주세요.")
    private List<Long> partIds;

    @NotNull(message = "경력(연차)을 입력해주세요.")
    @Min(value = 0, message = "경력은 0 이상이어야 합니다.")
    private Integer careerYears;

    private List<Long> techStackIds;

    private String oneLiner;
}
