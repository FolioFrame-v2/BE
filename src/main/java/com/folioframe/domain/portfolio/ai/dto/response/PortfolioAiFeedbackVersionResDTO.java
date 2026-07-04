package com.folioframe.domain.portfolio.ai.dto.response;

import com.folioframe.domain.portfolio.ai.entity.PortfolioAiFeedback;

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
        LocalDateTime lastModifiedAt,
        List<PortfolioAiFeedbackVersionResDTO> revisions
) {
    // 최상위/자식 버전 항목. 최상위 항목만 자식(수정본) 목록을 함께 담아 트리 형태로 내려준다.
    // lastModifiedAt: AI 첨삭 수신 여부와 무관하게, 필드 선택/직접수정으로 실제 내용이 마지막으로 바뀐 시각
    public static PortfolioAiFeedbackVersionResDTO from(PortfolioAiFeedback feedback, LocalDateTime lastModifiedAt,
                                                          List<PortfolioAiFeedbackVersionResDTO> revisions, boolean published) {
        return new PortfolioAiFeedbackVersionResDTO(
                feedback.getId(),
                feedback.getVersion(),
                feedback.getSubVersion(),
                feedback.getLabel(),
                feedback.getScore(),
                feedback.isFinalized(),
                published,
                feedback.getCreatedAt(),
                lastModifiedAt,
                revisions
        );
    }

    // AI 첨삭을 한 번도 요청한 적 없을 때의 가상 "원본" 목록 항목. 실제 AiFeedback 엔티티가 없어 id/생성일시/이름이 없고,
    // lastModifiedAt은 지금 라이브 콘텐츠가 마지막으로 저장된 시각(portfolio.lastSavedAt)을 그대로 쓴다.
    public static PortfolioAiFeedbackVersionResDTO original(boolean published, LocalDateTime lastModifiedAt) {
        return new PortfolioAiFeedbackVersionResDTO(null, 0, null, null, null, true, published, null, lastModifiedAt, List.of());
    }
}
