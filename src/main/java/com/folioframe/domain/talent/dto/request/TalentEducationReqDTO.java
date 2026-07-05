package com.folioframe.domain.talent.dto.request;

import com.folioframe.domain.portfolio.enums.DegreeType;
import com.folioframe.domain.portfolio.enums.EducationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TalentEducationReqDTO(

        @NotBlank(message = "학교명은 필수입니다.")
        @Size(max = 100, message = "학교명은 100자를 초과할 수 없습니다.")
        String schoolName,

        @Size(max = 100, message = "전공은 100자를 초과할 수 없습니다.")
        String major,

        DegreeType degree,

        LocalDate startedAt,

        LocalDate endedAt,

        @NotNull(message = "학력 상태는 필수입니다.")
        EducationStatus status
) {}
