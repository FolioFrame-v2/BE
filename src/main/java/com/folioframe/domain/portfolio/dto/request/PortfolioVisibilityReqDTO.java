package com.folioframe.domain.portfolio.dto.request;

import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
import jakarta.validation.constraints.NotNull;

public record PortfolioVisibilityReqDTO(
        @NotNull(message = "공개 설정은 필수입니다.")
        PortfolioVisibility visibility
) {}
