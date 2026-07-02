package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.enums.EditStatus;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;

import java.time.LocalDateTime;
import java.util.List;

public record PortfolioResDTO(
        Long id,
        Long talentProfileId,
        Long templateId,
        String templateName,
        String title,
        JobRole jobRole,
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
        LocalDateTime updatedAt,
        List<TechstackResDTO> techstacks
) {
    public static PortfolioResDTO from(Portfolio portfolio, List<Techstack> techstacks) {
        return new PortfolioResDTO(
                portfolio.getId(),
                portfolio.getTalentProfile().getId(),
                portfolio.getTemplate() != null ? portfolio.getTemplate().getId() : null,
                portfolio.getTemplate() != null ? portfolio.getTemplate().getName() : null,
                portfolio.getTitle(),
                portfolio.getJobRole(),
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
                portfolio.getUpdatedAt(),
                techstacks.stream().map(TechstackResDTO::from).toList()
        );
    }
}
