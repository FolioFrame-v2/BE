package com.folioframe.domain.talent.dto.response;

import com.folioframe.domain.common.dto.response.PartResDTO;
import com.folioframe.domain.common.enums.Gender;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class TalentProfileResDTO {
    private Long talentProfileId;

    private String name;
    private Long regionId;
    private String contactEmail;
    private String phoneNumber;
    private Integer age;
    private Gender gender;

    private String githubUrl;
    private String portfolioWebsite;

    private List<PartResDTO> parts;
    private Integer careerYears;

    private String oneLiner;

    private String createdAt;
    private String updatedAt;

    private List<TalentTechStackResDTO> techStacks;
    private List<TalentCareerResDTO> careers;
    private List<TalentEducationResDTO> educations;
    private List<TalentCertificateResDTO> certificates;
}
