package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.enums.EditStatus;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;

import java.time.LocalDateTime;

public record PortfolioResDTO(
        Long id,
        Long talentProfileId,
        Long templateId,
        String templateName,
        String title,
        JobRole jobRole,
        String careerSummary,
        String contactEmail,
        String githubUrl,
        String personalWebsite,
        String oneLiner,
        String description,
        PortfolioVisibility visibility,
        String publicSlug,
        int viewCount,
        int bookmarkCount,
        EditStatus editStatus,
        LocalDateTime lastSavedAt,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PortfolioResDTO from(Portfolio portfolio) {
        return new PortfolioResDTO(
                portfolio.getId(),
                portfolio.getTalentProfile().getId(),
                portfolio.getTemplate() != null ? portfolio.getTemplate().getId() : null,
                portfolio.getTemplate() != null ? portfolio.getTemplate().getName() : null,
                portfolio.getTitle(),
                portfolio.getJobRole(),
                portfolio.getCareerSummary(),
                portfolio.getContactEmail(),
                portfolio.getGithubUrl(),
                portfolio.getPersonalWebsite(),
                portfolio.getOneLiner(),
                portfolio.getDescription(),
                portfolio.getVisibility(),
                portfolio.getPublicSlug(),
                portfolio.getViewCount(),
                portfolio.getBookmarkCount(),
                portfolio.getEditStatus(),
                portfolio.getLastSavedAt(),
                portfolio.getPublishedAt(),
                portfolio.getCreatedAt(),
                portfolio.getUpdatedAt()
        );
    }
}
