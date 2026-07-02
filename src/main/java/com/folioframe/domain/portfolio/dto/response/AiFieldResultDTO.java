package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioAiField;
import com.folioframe.domain.portfolio.enums.AiChosenType;
import com.folioframe.domain.portfolio.enums.AiFieldTargetType;

public record AiFieldResultDTO(
        Long id,
        AiFieldTargetType targetType,
        Long portfolioFieldId,
        Long portfolioProjectId,
        String originalText,
        String aiRevisedText,
        AiChosenType chosen
) {
    public static AiFieldResultDTO from(PortfolioAiField field) {
        return new AiFieldResultDTO(
                field.getId(),
                field.getTargetType(),
                field.getPortfolioField() != null ? field.getPortfolioField().getId() : null,
                field.getPortfolioProject() != null ? field.getPortfolioProject().getId() : null,
                field.getOriginalText(),
                field.getAiRevisedText(),
                field.getChosen()
        );
    }
}
