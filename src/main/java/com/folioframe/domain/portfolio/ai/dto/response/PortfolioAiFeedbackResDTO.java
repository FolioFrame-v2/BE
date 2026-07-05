package com.folioframe.domain.portfolio.ai.dto.response;

import com.folioframe.domain.portfolio.ai.entity.PortfolioAiFeedback;
import com.folioframe.domain.portfolio.ai.enums.AiFeedbackStatus;

import java.time.LocalDateTime;
import java.util.List;

public record PortfolioAiFeedbackResDTO(
        Long id,
        Integer version,
        Integer subVersion,
        String label,
        String comment,
        Integer score,
        AiFeedbackStatus status,
        boolean finalized,
        boolean published,
        LocalDateTime createdAt,
        List<AiFieldResDTO> fields
) {
    public static PortfolioAiFeedbackResDTO of(PortfolioAiFeedback feedback, List<AiFieldResDTO> fields, boolean published) {
        return new PortfolioAiFeedbackResDTO(
                feedback.getId(),
                feedback.getVersion(),
                feedback.getSubVersion(),
                feedback.getLabel(),
                feedback.getComment(),
                feedback.getScore(),
                feedback.getStatus(),
                feedback.isFinalized(),
                published,
                feedback.getCreatedAt(),
                fields
        );
    }

    // AI 첨삭을 한 번도 요청한 적 없을 때의 "원본"(현재 라이브 필드 콘텐츠) 조회용.
    // 실제 AiFeedback이 아니므로 총평/점수/상태/이름(label)이 없다(저장할 곳이 없음).
    public static PortfolioAiFeedbackResDTO original(List<AiFieldResDTO> fields, boolean published) {
        return new PortfolioAiFeedbackResDTO(
                null,
                0,
                null,
                null,
                null,
                null,
                null,
                true,
                published,
                null,
                fields
        );
    }
}
