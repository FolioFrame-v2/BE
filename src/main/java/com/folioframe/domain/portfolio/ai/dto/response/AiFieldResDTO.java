package com.folioframe.domain.portfolio.ai.dto.response;

import com.folioframe.domain.portfolio.ai.entity.PortfolioAiField;
import com.folioframe.domain.portfolio.ai.enums.AiChosenType;
import com.folioframe.domain.portfolio.ai.enums.AiFieldTargetType;

public record AiFieldResDTO(
        Long id,
        AiFieldTargetType targetType,
        Long portfolioFieldId,
        Long portfolioProjectId,
        String originalText,
        String aiRevisedText,
        String resolvedText,
        AiChosenType chosen
) {
    public static AiFieldResDTO from(PortfolioAiField field) {
        return from(field, field.getResolvedText());
    }

    // 아직 확정되지 않은(열려있는) 버전은 실시간으로 계산한 resolvedText를 그대로 넘겨받는다.
    public static AiFieldResDTO from(PortfolioAiField field, String resolvedText) {
        return new AiFieldResDTO(
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
    public static AiFieldResDTO finalOnly(PortfolioAiField field) {
        return new AiFieldResDTO(
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

    // "원본"(현재 라이브 필드 콘텐츠) 조회용. AI 첨삭 이력과 무관하게 지금의 실제 콘텐츠만 노출한다.
    public static AiFieldResDTO liveOriginal(AiFieldTargetType targetType, Long portfolioFieldId,
                                                 Long portfolioProjectId, String content) {
        return new AiFieldResDTO(
                null,
                targetType,
                portfolioFieldId,
                portfolioProjectId,
                content,
                null,
                null,
                null
        );
    }
}
