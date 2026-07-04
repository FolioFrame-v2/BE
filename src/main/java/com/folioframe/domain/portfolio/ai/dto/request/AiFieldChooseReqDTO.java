package com.folioframe.domain.portfolio.ai.dto.request;

import com.folioframe.domain.portfolio.ai.enums.AiChosenType;
import jakarta.validation.constraints.NotNull;

public record AiFieldChooseReqDTO(

        @NotNull(message = "선택 값은 필수입니다.")
        AiChosenType chosen
) {}
