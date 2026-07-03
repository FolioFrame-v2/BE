package com.folioframe.domain.talent.dto.request;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.talent.enums.ProfileVisibility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class TalentProfileCreateRequest {
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
    private List<Long> techStackIds;
    private List<Long> tagIds;
}