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
        String resolvedText,
        AiChosenType chosen
) {
    public static AiFieldResultDTO from(PortfolioAiField field) {
        return from(field, field.getResolvedText());
    }

    // 아직 확정되지 않은(열려있는) 버전은 실시간으로 계산한 resolvedText를 그대로 넘겨받는다.
    public static AiFieldResultDTO from(PortfolioAiField field, String resolvedText) {
        return new AiFieldResultDTO(
                field.getId(),
                field.getTargetType(),
                field.getPortfolioField() != null ? field.getPortfolioField().getId() : null,
                field.getPortfolioProject() != null ? field.getPortfolioProject().getId() : null,
                field.getOriginalText(),
                field.getAiRevisedText(),
                resolvedText,
                field.getChosen()
        );
    }

    // 확정된(닫힌) 버전은 더 이상 선택 UX가 없으므로 최종 확정 내용(resolvedText)만 노출한다.
    public static AiFieldResultDTO finalOnly(PortfolioAiField field) {
        return new AiFieldResultDTO(
                field.getId(),
                field.getTargetType(),
                field.getPortfolioField() != null ? field.getPortfolioField().getId() : null,
                field.getPortfolioProject() != null ? field.getPortfolioProject().getId() : null,
                null,
                null,
                field.getResolvedText(),
                null
        );
    }

    // "원본"(첫 AI 첨삭 요청 직전 상태) 조회용. version 1의 AiField가 기록해 둔 originalText만 노출한다.
    public static AiFieldResultDTO originalOnly(PortfolioAiField field) {
        return new AiFieldResultDTO(
                field.getId(),
                field.getTargetType(),
                field.getPortfolioField() != null ? field.getPortfolioField().getId() : null,
                field.getPortfolioProject() != null ? field.getPortfolioProject().getId() : null,
                field.getOriginalText(),
                null,
                null,
                null
        );
    }
}
