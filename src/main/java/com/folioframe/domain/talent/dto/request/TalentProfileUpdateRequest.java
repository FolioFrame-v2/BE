package com.folioframe.domain.talent.dto.request;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.Gender;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.job.enums.EmploymentType;
import com.folioframe.domain.job.enums.JobSeekingStatus;
import com.folioframe.domain.talent.enums.ProfileVisibility;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class TalentProfileUpdateRequest {

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
    private String linkedinUrl;

    @NotBlank(message = "지원 분야를 선택해주세요.")
    private String applicationField;

    @NotNull(message = "파트를 선택해주세요.")
    private JobRole jobRole;

    @NotNull(message = "경력을 선택해주세요.")
    private CareerLevel careerLevel;

    private EmploymentType employmentType;

    private List<Long> techStackIds;

    private String oneLiner;
    private String introduction;

    @NotNull(message = "프로필 공개 범위를 선택해주세요.")
    private ProfileVisibility profileVisibility;

    @NotNull(message = "구직 상태를 선택해주세요.")
    private JobSeekingStatus jobSeekingStatus;
}