package com.folioframe.domain.portfolio.ai.dto.client;

import java.util.List;

// FolioFrame_AI(FastAPI)의 FeedbackResponse 스키마와 1:1 대응
public record AiFeedbackApiResDTO(
        String comment,
        Integer score,
        List<AiFieldRevisionResDTO> fields
) {}
