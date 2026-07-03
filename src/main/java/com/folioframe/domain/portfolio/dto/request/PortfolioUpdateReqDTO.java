package com.folioframe.domain.portfolio.dto.request;

import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PortfolioUpdateReqDTO(

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
        String title,

        JobRole jobRole,

        @Size(max = 500, message = "한줄 소개는 500자를 초과할 수 없습니다.")
        String oneLiner,

        @Size(max = 500, message = "상세 설명은 500자를 초과할 수 없습니다.")
        String description,

        PortfolioVisibility visibility,

        // 포트폴리오 전체에서 사용해본 기술스택 ID 목록
        List<Long> techstackIds
) {}
