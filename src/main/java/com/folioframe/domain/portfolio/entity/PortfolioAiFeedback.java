package com.folioframe.domain.portfolio.entity;

import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.portfolio.enums.AiFeedbackStatus;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_ai_feedback")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioAiFeedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_ai_feedback_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "version")
    private Integer version;

    @Column(name = "overall_comment", columnDefinition = "TEXT")
    private String comment;

    // 0~100, null 이면 점수 없음
    @Column(name = "score")
    private Integer score;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AiFeedbackStatus status = AiFeedbackStatus.PENDING;

    // null이면 아직 열려있는(선택 변경 가능한) 버전, 값이 있으면 저장/업로드/재첨삭 등으로 확정되어
    // 더 이상 필드 선택을 바꿀 수 없는 버전
    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    public void markFinalized(LocalDateTime finalizedAt) {
        this.finalizedAt = finalizedAt;
    }

    public boolean isFinalized() {
        return finalizedAt != null;
    }
}
