package com.folioframe.domain.portfolio.ai.dto.request;

import jakarta.validation.constraints.NotNull;

public record AiFieldEditReqDTO(

        @NotNull(message = "content는 필수입니다.")
        String content
) {}
