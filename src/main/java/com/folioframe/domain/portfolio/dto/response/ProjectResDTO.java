package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.portfolio.entity.PortfolioProject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectResDTO(
        Long id,
        Long portfolioId,
        String title,
        String role,
        String content,
        String projectUrl,
        LocalDate startedAt,
        LocalDate endedAt,
        List<TechstackResDTO> techstacks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProjectResDTO from(PortfolioProject project, List<Techstack> techstacks) {
        return new ProjectResDTO(
                project.getId(),
                project.getPortfolio().getId(),
                project.getTitle(),
                project.getRole(),
                project.getContent(),
                project.getProjectUrl(),
                project.getStartedAt(),
                project.getEndedAt(),
                techstacks.stream().map(TechstackResDTO::from).toList(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
