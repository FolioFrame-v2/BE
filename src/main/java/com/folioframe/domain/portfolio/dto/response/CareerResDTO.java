package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioCareer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CareerResDTO(
        Long id,
        Long portfolioId,
        String companyName,
        String position,
        String description,
        LocalDate startedAt,
        LocalDate endedAt,
        boolean current,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CareerResDTO from(PortfolioCareer career) {
        return new CareerResDTO(
                career.getId(),
                career.getPortfolio().getId(),
                career.getCompanyName(),
                career.getPosition(),
                career.getDescription(),
                career.getStartedAt(),
                career.getEndedAt(),
                career.isCurrent(),
                career.getCreatedAt(),
                career.getUpdatedAt()
        );
    }
}
