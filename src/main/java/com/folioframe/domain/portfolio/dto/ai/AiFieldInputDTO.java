package com.folioframe.domain.portfolio.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.folioframe.domain.portfolio.enums.AiFieldTargetType;

// FolioFrame_AI(FastAPI)의 FieldInput 스키마와 1:1 대응
public record AiFieldInputDTO(
        @JsonProperty("field_id") Long fieldId,
        @JsonProperty("field_type") AiFieldTargetType fieldType,
        String title,
        String description,
        String content
) {}
