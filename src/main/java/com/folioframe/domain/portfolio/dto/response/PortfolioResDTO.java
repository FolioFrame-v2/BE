package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
import com.folioframe.domain.portfolio.enums.TemplateLayoutKey;

import java.time.LocalDateTime;
import java.util.List;

public record PortfolioResDTO(
        Long id,
        Long talentProfileId,
        Long templateId,
        String templateName,
        TemplateLayoutKey templateLayoutKey,
        String title,
        JobRole jobRole,
        String oneLiner,
        String description,
        PortfolioVisibility visibility,
        String publicSlug,
        int viewCount,
        int bookmarkCount,
        LocalDateTime lastSavedAt,
        LocalDateTime publishedAt,
        LocalDateTime confirmedAt,
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
                portfolio.getTemplate() != null ? portfolio.getTemplate().getLayoutKey() : null,
                portfolio.getTitle(),
                portfolio.getJobRole(),
                portfolio.getOneLiner(),
                portfolio.getDescription(),
                portfolio.getVisibility(),
                portfolio.getPublicSlug(),
                portfolio.getViewCount(),
                portfolio.getBookmarkCount(),
                portfolio.getLastSavedAt(),
                portfolio.getPublishedAt(),
                portfolio.getConfirmedAt(),
                portfolio.getCreatedAt(),
                portfolio.getUpdatedAt(),
                techstacks.stream().map(TechstackResDTO::from).toList()
        );
    }
}
