package com.folioframe.domain.talent.dto.response;

import com.folioframe.domain.portfolio.enums.DegreeType;
import com.folioframe.domain.portfolio.enums.EducationStatus;
import com.folioframe.domain.talent.entity.TalentEducation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TalentEducationResDTO(
        Long id,
        Long talentProfileId,
        String schoolName,
        String major,
        DegreeType degree,
        LocalDate startedAt,
        LocalDate endedAt,
        EducationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TalentEducationResDTO from(TalentEducation education) {
        return new TalentEducationResDTO(
                education.getId(),
                education.getTalentProfile().getId(),
                education.getSchoolName(),
                education.getMajor(),
                education.getDegree(),
                education.getStartedAt(),
                education.getEndedAt(),
                education.getStatus(),
                education.getCreatedAt(),
                education.getUpdatedAt()
        );
    }
}
