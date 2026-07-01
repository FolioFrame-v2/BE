package com.folioframe.domain.portfolio.dto.request;

import jakarta.validation.constraints.Size;

public record PortfolioFieldUpdateReqDTO(
        @Size(max = 500, message = "내용은 500자를 초과할 수 없습니다.")
        String content
) {}
