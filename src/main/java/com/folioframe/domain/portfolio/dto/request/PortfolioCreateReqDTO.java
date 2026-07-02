package com.folioframe.domain.portfolio.dto.request;

import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PortfolioCreateReqDTO(

        @NotNull(message = "템플릿 선택은 필수입니다.")
        @Schema(example = "1")
        Long templateId,

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
        String title,

        JobRole jobRole,

        @Size(max = 500, message = "한줄 소개는 500자를 초과할 수 없습니다.")
        String oneLiner,

        @Size(max = 500, message = "상세 설명은 500자를 초과할 수 없습니다.")
        String description,

        PortfolioVisibility visibility
) {}
