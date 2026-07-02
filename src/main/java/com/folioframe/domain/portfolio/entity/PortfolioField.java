package com.folioframe.domain.portfolio.entity;

import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "portfolio_field")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioField extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_field_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Portfolio portfolio;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    // 관리자가 템플릿 필드 생성 시 남긴 작성 안내 (TemplateField.description 복사본)
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // 사용자가 적용한 AI 피드백 버전
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applied_feedback_id")
    private PortfolioAiFeedback appliedFeedback;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    public void updateContent(String content) {
        this.content = content;
    }
}
