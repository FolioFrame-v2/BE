package com.folioframe.domain.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CareerReqDTO(

        @NotBlank(message = "회사명은 필수입니다.")
        @Size(max = 100, message = "회사명은 100자를 초과할 수 없습니다.")
        String companyName,

        @Size(max = 100, message = "직책은 100자를 초과할 수 없습니다.")
        String position,

        String description,

        @NotNull(message = "입사일은 필수입니다.")
        LocalDate startedAt,

        LocalDate endedAt
) {}
