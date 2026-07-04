package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.common.dto.response.RegionResDTO;
import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.talent.entity.TalentProfile;

import java.util.List;

public record PortfolioPublicListResDTO(
        Long id,
        String title,
        String authorName,
        RegionResDTO authorRegion,
        CareerLevel careerLevel,
        JobRole jobRole,
        List<TechstackResDTO> techstacks,
        int bookmarkCount,
        int viewCount
) {
    public static PortfolioPublicListResDTO from(Portfolio portfolio, List<Techstack> techstacks) {
        TalentProfile talentProfile = portfolio.getTalentProfile();
        return new PortfolioPublicListResDTO(
                portfolio.getId(),
                portfolio.getTitle(),
                talentProfile.getMember().getName(),
                RegionResDTO.from(talentProfile.getRegion()),
                talentProfile.getCareerLevel(),
                portfolio.getJobRole(),
                techstacks.stream().map(TechstackResDTO::from).toList(),
                portfolio.getBookmarkCount(),
                portfolio.getViewCount()
        );
    }
}
