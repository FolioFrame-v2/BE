package com.folioframe.domain.talent.dto.response;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.talent.enums.ProfileVisibility;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class TalentProfileResponse {
    private Long talentProfileId;
    private String profileImageUrl;
    private String jobTitle;
    private String oneLiner;
    private String contactEmail;
    private String githubUrl;
    private String portfolioWebsite;
    private String currentCompany;
    private String currentPosition;
    private Integer careerYears;
    private CareerLevel careerLevel;
    private Long regionId;
    private String jobSeekingStatus;
    private ProfileVisibility profileVisibility;
    private String createdAt;
    private String updatedAt;
    private List<TalentTechStackResponse> techStacks;
    private List<TalentTagResponse> tags;
}