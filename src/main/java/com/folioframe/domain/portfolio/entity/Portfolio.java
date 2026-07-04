package com.folioframe.domain.portfolio.entity;

import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.ai.entity.PortfolioAiFeedback;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Portfolio extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talent_profile_id", nullable = false)
    private TalentProfile talentProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private PortfolioTemplate template;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_role")
    private JobRole jobRole;

    @Column(name = "one_liner", length = 500)
    private String oneLiner;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private PortfolioVisibility visibility = PortfolioVisibility.PRIVATE;

    @Column(name = "public_slug", unique = true, length = 100)
    private String publicSlug;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Builder.Default
    @Column(name = "ai_check_used_count", nullable = false)
    private int aiCheckUsedCount = 0;

    @Builder.Default
    @Column(name = "ai_check_max_count", nullable = false)
    private int aiCheckMaxCount = 3;

    @Column(name = "last_saved_at")
    private LocalDateTime lastSavedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    // 지금 라이브 콘텐츠가 어느 AI 첨삭 버전에서 게시된 것인지. 버전 개념 없이 publish()로 게시한
    // 경우엔 null로 남는다. 이 버전이 삭제되면 참조만 끊긴다(실제 게시 해제는 별도로 처리해야 함).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_feedback_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private PortfolioAiFeedback publishedFeedback;

    @Builder.Default
    @Column(name = "bookmark_count", nullable = false)
    private int bookmarkCount = 0;

    // 저장 확정 시각. 원본(v0) 스냅샷 생성/저장 API/게시 중 가장 먼저 일어나는 쪽에서 채워진다.
    // null이면 아직 한 번도 확정되지 않은 초안이라는 뜻이고, 방치 정리 스케줄러의 삭제 대상이 된다.
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    public void updateInfo(String title, JobRole jobRole, String oneLiner,
                           String description, PortfolioVisibility visibility) {
        this.title = title;
        this.jobRole = jobRole;
        this.oneLiner = oneLiner;
        this.description = description;
        this.visibility = visibility;
    }

    // 게시("게시하기" 버튼) = 편집 화면에서 선택된 버전(원본 포함)의 콘텐츠를 라이브에 반영하고
    // 확정한다. 공개/비공개 여부는 강제로 바꾸지 않고, 그 직전에 사용자가 선택해둔 visibility
    // (changeVisibility로 이미 반영된 값, 기본 PRIVATE)를 그대로 따른다 — 비공개를 선택한 채로도
    // 게시할 수 있다.
    public void publish() {
        this.publishedAt = LocalDateTime.now();
        this.lastSavedAt = LocalDateTime.now();
        confirmSave();
    }

    // 특정 AI 첨삭 버전을 게시. 그 버전의 콘텐츠가 이미 라이브에 복사된 뒤 호출된다.
    public void publishFeedback(PortfolioAiFeedback feedback) {
        this.publishedAt = LocalDateTime.now();
        this.lastSavedAt = LocalDateTime.now();
        this.publishedFeedback = feedback;
        confirmSave();
    }

    // 게시 해제 = 비공개 전환. 라이브 콘텐츠 초기화는 호출부(서비스)에서 별도로 처리한다.
    public void unpublish() {
        this.visibility = PortfolioVisibility.PRIVATE;
        this.publishedFeedback = null;
    }

    public void markSaved() {
        this.lastSavedAt = LocalDateTime.now();
    }

    // 공개/비공개 "선택"만 반영한다(게시 흐름을 거치지 않는 수동 전환이므로 게시 버전 추적은
    // 무의미해져 끊는다). 저장(확정) 전 초안 단계에서 이 선택만 하고 게시하기를 누르지 않은 채
    // 나가면 방치 정리 대상이어야 하므로, 여기서는 confirmSave()를 호출하지 않는다 — 실제 확정은
    // "게시하기"(publish/publishFeedback) 또는 저장 API를 통해서만 일어난다.
    public void changeVisibility(PortfolioVisibility visibility) {
        this.visibility = visibility;
        this.publishedFeedback = null;
    }

    // 저장 확정. 원본(v0) 스냅샷 생성/저장 API/게시 중 가장 먼저 호출되는 쪽에서 진입점 역할을 한다.
    // 이미 확정된 뒤 재호출은 무해하다(멱등).
    public void confirmSave() {
        if (this.confirmedAt == null) {
            this.confirmedAt = LocalDateTime.now();
        }
    }

    public void increaseAiCheckUsedCount() {
        this.aiCheckUsedCount++;
    }

    public void updateOneLiner(String oneLiner) {
        this.oneLiner = oneLiner;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void increaseBookmarkCount() {
        this.bookmarkCount++;
    }

    public void decreaseBookmarkCount() {
        if (this.bookmarkCount > 0) {
            this.bookmarkCount--;
        }
    }
}
