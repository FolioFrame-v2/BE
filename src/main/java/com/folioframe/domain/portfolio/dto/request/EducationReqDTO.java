package com.folioframe.domain.portfolio.dto.request;

import com.folioframe.domain.portfolio.enums.DegreeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EducationReqDTO(

        @NotBlank(message = "학교명은 필수입니다.")
        @Size(max = 100, message = "학교명은 100자를 초과할 수 없습니다.")
        String schoolName,

        @Size(max = 100, message = "전공은 100자를 초과할 수 없습니다.")
        String major,

        DegreeType degree,

        LocalDate startedAt,

        LocalDate endedAt,

        boolean graduated
) {}
