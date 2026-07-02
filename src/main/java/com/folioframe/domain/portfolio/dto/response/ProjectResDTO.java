package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioProject;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResDTO(
        Long id,
        Long portfolioId,
        String title,
        String role,
        String content,
        String thumbnailUrl,
        String projectUrl,
        LocalDate startedAt,
        LocalDate endedAt,
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
                project.getStartedAt(),
                project.getEndedAt(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
