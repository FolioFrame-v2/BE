package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioAiFeedback;

import java.time.LocalDateTime;
import java.util.List;

public record PortfolioAiFeedbackVersionResDTO(
        Long id,
        Integer version,
        Integer subVersion,
        String label,
        Integer score,
        boolean finalized,
        boolean published,
        LocalDateTime createdAt,
        List<PortfolioAiFeedbackVersionResDTO> revisions
) {
    // 최상위/자식 버전 항목. 최상위 항목만 자식(수정본) 목록을 함께 담아 트리 형태로 내려준다.
    public static PortfolioAiFeedbackVersionResDTO from(PortfolioAiFeedback feedback, List<PortfolioAiFeedbackVersionResDTO> revisions, boolean published) {
        return new PortfolioAiFeedbackVersionResDTO(
                feedback.getId(),
                feedback.getVersion(),
                feedback.getSubVersion(),
                feedback.getLabel(),
                feedback.getScore(),
                feedback.isFinalized(),
                published,
                feedback.getCreatedAt(),
                revisions
        );
    }

    // AI 첨삭을 한 번도 요청한 적 없을 때의 가상 "원본" 목록 항목. 실제 AiFeedback 엔티티가 없어 id/생성일시/이름이 없다.
    public static PortfolioAiFeedbackVersionResDTO original(boolean published) {
        return new PortfolioAiFeedbackVersionResDTO(null, 0, null, null, null, true, published, null, List.of());
    }
}
