package com.folioframe.domain.talent.dto.response;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.Gender;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.job.enums.EmploymentType;
import com.folioframe.domain.talent.enums.ProfileVisibility;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class TalentProfileResponse {
    private Long talentProfileId;

    private String name;
    private Long regionId;
    private String contactEmail;
    private String phoneNumber;
    private Integer age;
    private Gender gender;

    private String githubUrl;
    private String portfolioWebsite;
    private String linkedinUrl;

    private String applicationField;
    private JobRole jobRole;
    private CareerLevel careerLevel;
    private EmploymentType employmentType;

    private String oneLiner;
    private String introduction;

    private ProfileVisibility profileVisibility;
    private String profileImageUrl;
    private String jobSeekingStatus;

    private String createdAt;
    private String updatedAt;

    private List<TalentTechStackResponse> techStacks;
    private List<TalentTagResponse> tags;
}