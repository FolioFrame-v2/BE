package com.folioframe.domain.portfolio.ai.dto.request;

import jakarta.validation.constraints.Size;

public record AiFeedbackRenameReqDTO(

        @Size(max = 50, message = "버전 이름은 50자를 넘을 수 없습니다.")
        String label
) {}
