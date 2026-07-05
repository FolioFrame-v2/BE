package com.folioframe.domain.talent.dto.response;

import com.folioframe.domain.talent.entity.TalentCareer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TalentCareerResDTO(
        Long id,
        Long talentProfileId,
        String companyName,
        String position,
        String description,
        LocalDate startedAt,
        LocalDate endedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TalentCareerResDTO from(TalentCareer career) {
        return new TalentCareerResDTO(
                career.getId(),
                career.getTalentProfile().getId(),
                career.getCompanyName(),
                career.getPosition(),
                career.getDescription(),
                career.getStartedAt(),
                career.getEndedAt(),
                career.getCreatedAt(),
                career.getUpdatedAt()
        );
    }
}
