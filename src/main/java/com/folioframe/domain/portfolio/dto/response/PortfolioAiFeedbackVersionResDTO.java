package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioAiFeedback;

import java.time.LocalDateTime;

public record PortfolioAiFeedbackVersionResDTO(
        Long id,
        Integer version,
        Integer score,
        LocalDateTime createdAt
) {
    public static PortfolioAiFeedbackVersionResDTO from(PortfolioAiFeedback feedback) {
        return new PortfolioAiFeedbackVersionResDTO(
                feedback.getId(),
                feedback.getVersion(),
                feedback.getScore(),
                feedback.getCreatedAt()
        );
    }

    // "원본"(첫 AI 첨삭 요청 직전 상태) 목록 항목. 실제 AiFeedback 엔티티가 없어 id/생성일시가 없다.
    public static PortfolioAiFeedbackVersionResDTO original() {
        return new PortfolioAiFeedbackVersionResDTO(null, 0, null, null);
    }
}
