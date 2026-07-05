package com.folioframe.domain.portfolio.ai.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.folioframe.domain.portfolio.ai.enums.AiFieldTargetType;

// FolioFrame_AI(FastAPI)의 FieldRevision 스키마와 1:1 대응
public record AiFieldRevisionResDTO(
        @JsonProperty("field_id") Long fieldId,
        @JsonProperty("field_type") AiFieldTargetType fieldType,
        @JsonProperty("ai_revised_text") String aiRevisedText
) {}
