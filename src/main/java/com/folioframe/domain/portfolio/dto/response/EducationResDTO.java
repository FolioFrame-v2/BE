package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioEducation;
import com.folioframe.domain.portfolio.enums.DegreeType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EducationResDTO(
        Long id,
        Long portfolioId,
        String schoolName,
        String major,
        DegreeType degree,
        LocalDate startedAt,
        LocalDate endedAt,
        boolean graduated,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EducationResDTO from(PortfolioEducation education) {
        return new EducationResDTO(
                education.getId(),
                education.getPortfolio().getId(),
                education.getSchoolName(),
                education.getMajor(),
                education.getDegree(),
                education.getStartedAt(),
                education.getEndedAt(),
                education.isGraduated(),
                education.getCreatedAt(),
                education.getUpdatedAt()
        );
    }
}
