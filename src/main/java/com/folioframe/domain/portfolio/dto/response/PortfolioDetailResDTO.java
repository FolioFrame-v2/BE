package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioCareer;
import com.folioframe.domain.portfolio.entity.PortfolioCertificate;
import com.folioframe.domain.portfolio.entity.PortfolioEducation;
import com.folioframe.domain.portfolio.entity.PortfolioField;
import com.folioframe.domain.portfolio.entity.PortfolioProject;
import com.folioframe.domain.portfolio.enums.EditStatus;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record PortfolioDetailResDTO(
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
        List<PortfolioFieldResDTO> fields,
        List<EducationResDTO> educations,
        List<CareerResDTO> careers,
        List<CertificateResDTO> certificates,
        List<ProjectResDTO> projects,
        List<TechstackResDTO> techstacks,
        TalentProfileSummaryResDTO talentProfile
) {
    public static PortfolioDetailResDTO of(
            Portfolio portfolio,
            List<PortfolioField> fields,
            List<PortfolioEducation> educations,
            List<PortfolioCareer> careers,
            List<PortfolioCertificate> certificates,
            List<PortfolioProject> projects,
            Map<Long, List<Techstack>> techstacksByProjectId,
            List<Techstack> techstacks
    ) {
        return new PortfolioDetailResDTO(
                portfolio.getId(),
                portfolio.getTalentProfile().getId(),
                portfolio.getTemplate().getId(),
                portfolio.getTemplate().getName(),
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
                fields.stream().map(PortfolioFieldResDTO::from).toList(),
                educations.stream().map(EducationResDTO::from).toList(),
                careers.stream().map(CareerResDTO::from).toList(),
                certificates.stream().map(CertificateResDTO::from).toList(),
                projects.stream()
                        .map(project -> ProjectResDTO.from(project, techstacksByProjectId.getOrDefault(project.getId(), List.of())))
                        .toList(),
                techstacks.stream().map(TechstackResDTO::from).toList(),
                TalentProfileSummaryResDTO.from(portfolio.getTalentProfile())
        );
    }
}
