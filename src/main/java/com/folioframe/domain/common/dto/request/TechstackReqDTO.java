package com.folioframe.domain.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TechstackReqDTO(
        @NotBlank(message = "기술스택 이름은 필수입니다.")
        @Size(max = 50, message = "기술스택 이름은 50자를 초과할 수 없습니다.")
        String name
) {}
