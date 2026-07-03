package com.folioframe.domain.portfolio.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// FolioFrame_AI(FastAPI)의 FeedbackRequest 스키마와 1:1 대응
public record AiFeedbackApiReqDTO(
        @JsonProperty("portfolio_title") String portfolioTitle,
        @JsonProperty("job_role") String jobRole,
        List<AiFieldInputDTO> fields
) {}
