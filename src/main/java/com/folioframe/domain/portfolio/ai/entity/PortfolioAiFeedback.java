package com.folioframe.domain.portfolio.ai.entity;

import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.portfolio.ai.enums.AiFeedbackStatus;
import com.folioframe.domain.portfolio.entity.Portfolio;
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

    // 최상위 버전 번호. 자식(수정본) 버전은 부모와 같은 값을 공유하고 subVersion으로 구분한다.
    @Column(name = "version")
    private Integer version;

    // null이면 최상위 버전, 값이 있으면 parentFeedback 아래의 몇 번째 수정본인지를 나타낸다.
    @Column(name = "sub_version")
    private Integer subVersion;

    // "수정본 만들기"로 생성된 자식 버전일 때만 값이 있다. 자식은 AI를 다시 호출하지 않고
    // 부모의 확정 콘텐츠를 그대로 복사해 직접 수정만 가능한 버전이다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_feedback_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PortfolioAiFeedback parentFeedback;

    // 사용자가 버전 목록 패널에서 직접 지정한 표시 이름. null이면 FE가 "버전1", "버전1-1" 같은
    // 기본 이름을 만들어 보여준다.
    @Column(name = "label", length = 50)
    private String label;

    @Column(name = "overall_comment", columnDefinition = "TEXT")
    private String comment;

    // 0~100, null 이면 점수 없음
    @Column(name = "score")
    private Integer score;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AiFeedbackStatus status = AiFeedbackStatus.PENDING;

    // null이면 아직 열려있는(선택 변경 가능한) 버전, 값이 있으면 명시적 저장 API 호출로 확정되어
    // 더 이상 필드 선택을 바꿀 수 없는 버전. 자식(수정본) 버전은 생성 즉시 값이 채워진다.
    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    public void markFinalized(LocalDateTime finalizedAt) {
        this.finalizedAt = finalizedAt;
    }

    public void rename(String label) {
        this.label = (label == null || label.isBlank()) ? null : label;
    }

    public boolean isFinalized() {
        return finalizedAt != null;
    }

    public boolean isTopLevel() {
        return parentFeedback == null;
    }
}
