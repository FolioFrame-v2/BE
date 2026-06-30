package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioProject;
import com.folioframe.domain.portfolio.enums.ProjectDuration;

import java.time.LocalDateTime;

public record ProjectResDTO(
        Long id,
        Long portfolioId,
        String title,
        String role,
        String content,
        String thumbnailUrl,
        String projectUrl,
        ProjectDuration durationRange,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProjectResDTO from(PortfolioProject project) {
        return new ProjectResDTO(
                project.getId(),
                project.getPortfolio().getId(),
                project.getTitle(),
                project.getRole(),
                project.getContent(),
                project.getThumbnailUrl(),
                project.getProjectUrl(),
                project.getDurationRange(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
