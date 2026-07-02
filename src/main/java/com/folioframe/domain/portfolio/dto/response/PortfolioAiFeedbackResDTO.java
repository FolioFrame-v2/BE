package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioAiFeedback;
import com.folioframe.domain.portfolio.enums.AiFeedbackStatus;

import java.time.LocalDateTime;
import java.util.List;

public record PortfolioAiFeedbackResDTO(
        Long id,
        Integer version,
        String comment,
        Integer score,
        AiFeedbackStatus status,
        boolean finalized,
        LocalDateTime createdAt,
        List<AiFieldResultDTO> fields
) {
    public static PortfolioAiFeedbackResDTO of(PortfolioAiFeedback feedback, List<AiFieldResultDTO> fields) {
        return new PortfolioAiFeedbackResDTO(
                feedback.getId(),
                feedback.getVersion(),
                feedback.getComment(),
                feedback.getScore(),
                feedback.getStatus(),
                feedback.isFinalized(),
                feedback.getCreatedAt(),
                fields
        );
    }

    // "원본"(첫 AI 첨삭 요청 직전 상태) 조회용. 실제 AiFeedback이 아니므로 총평/점수/상태가 없다.
    public static PortfolioAiFeedbackResDTO original(List<AiFieldResultDTO> fields) {
        return new PortfolioAiFeedbackResDTO(
                null,
                0,
                null,
                null,
                null,
                true,
                null,
                fields
        );
    }
}
