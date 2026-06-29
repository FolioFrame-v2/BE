package com.folioframe.domain.portfolio.entity;

import com.folioframe.domain.portfolio.enums.AiChosenType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_field_id", nullable = false)
    private PortfolioField portfolioField;

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @Column(name = "ai_revised_text", columnDefinition = "TEXT")
    private String aiRevisedText;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "chosen", nullable = false)
    private AiChosenType chosen = AiChosenType.PENDING;
}
