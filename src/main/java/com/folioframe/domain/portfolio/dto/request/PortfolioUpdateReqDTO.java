package com.folioframe.domain.portfolio.dto.request;

import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PortfolioUpdateReqDTO(

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
        String title,

        JobRole jobRole,

        String careerSummary,

        @Size(max = 100, message = "연락처 이메일은 100자를 초과할 수 없습니다.")
        String contactEmail,

        @Size(max = 500, message = "GitHub URL은 500자를 초과할 수 없습니다.")
        String githubUrl,

        @Size(max = 500, message = "개인 웹사이트 URL은 500자를 초과할 수 없습니다.")
        String personalWebsite,

        @Size(max = 200, message = "한줄 소개는 200자를 초과할 수 없습니다.")
        String oneLiner,

        String description,

        PortfolioVisibility visibility
) {}
