package com.folioframe.domain.portfolio.entity;

import com.folioframe.domain.portfolio.enums.AiChosenType;
import com.folioframe.domain.portfolio.enums.AiFieldTargetType;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "portfolio_ai_field")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioAiField extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_ai_field_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id", nullable = false)
    private PortfolioAiFeedback feedback;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private AiFieldTargetType targetType = AiFieldTargetType.CUSTOM_FIELD;

    // targetType == CUSTOM_FIELD 일 때만 값 존재
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_field_id")
    private PortfolioField portfolioField;

    // targetType == PROJECT_SUMMARY 일 때만 값 존재
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_project_id")
    private PortfolioProject portfolioProject;

    // PORTFOLIO_ONE_LINER, PORTFOLIO_DESCRIPTION은 feedback.portfolio,
    // PROFILE_ONE_LINER는 feedback.portfolio.talentProfile로 대상을 특정할 수 있어
    // 별도 참조 컬럼이 필요 없다.

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @Column(name = "ai_revised_text", columnDefinition = "TEXT")
    private String aiRevisedText;

    // 이 버전이 "마감"되는 시점(다음 AI 첨삭 시작 시)의 실제 최종 텍스트.
    // chosen/이후 수동 수정 결과까지 반영된, 버전 이력 조회 시 보여줄 확정본.
    @Column(name = "resolved_text", columnDefinition = "TEXT")
    private String resolvedText;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "chosen", nullable = false)
    private AiChosenType chosen = AiChosenType.PENDING;

    public void choose(AiChosenType chosen) {
        this.chosen = chosen;
    }

    public void updateResolvedText(String resolvedText) {
        this.resolvedText = resolvedText;
    }
}
