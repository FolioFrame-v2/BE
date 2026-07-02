package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioAiFeedback;
import com.folioframe.domain.portfolio.entity.PortfolioAiField;
import com.folioframe.domain.portfolio.enums.AiFeedbackStatus;

import java.time.LocalDateTime;
import java.util.List;

public record PortfolioAiFeedbackResDTO(
        Long id,
        Integer version,
        String comment,
        Integer score,
        AiFeedbackStatus status,
        LocalDateTime createdAt,
        List<AiFieldResultDTO> fields
) {
    public static PortfolioAiFeedbackResDTO of(PortfolioAiFeedback feedback, List<PortfolioAiField> fields) {
        return new PortfolioAiFeedbackResDTO(
                feedback.getId(),
                feedback.getVersion(),
                feedback.getComment(),
                feedback.getScore(),
                feedback.getStatus(),
                feedback.getCreatedAt(),
                fields.stream().map(AiFieldResultDTO::from).toList()
        );
    }
}
