package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.common.dto.response.RegionResDTO;
import com.folioframe.domain.talent.entity.TalentProfile;

public record TalentProfileSummaryResDTO(
        String oneLiner,
        String contactEmail,
        String githubUrl,
        String portfolioWebsite,
        RegionResDTO region
) {
    public static TalentProfileSummaryResDTO from(TalentProfile talentProfile) {
        return new TalentProfileSummaryResDTO(
                talentProfile.getOneLiner(),
                talentProfile.getContactEmail(),
                talentProfile.getGithubUrl(),
                talentProfile.getPortfolioWebsite(),
                RegionResDTO.from(talentProfile.getRegion())
        );
    }
}
