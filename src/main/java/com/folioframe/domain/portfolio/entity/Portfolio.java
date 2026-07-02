package com.folioframe.domain.portfolio.entity;

import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.enums.EditStatus;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "edit_status", nullable = false)
    private EditStatus editStatus = EditStatus.DRAFT;

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

    @Builder.Default
    @Column(name = "bookmark_count", nullable = false)
    private int bookmarkCount = 0;

    public void updateInfo(String title, JobRole jobRole, String oneLiner,
                           String description, PortfolioVisibility visibility) {
        this.title = title;
        this.jobRole = jobRole;
        this.oneLiner = oneLiner;
        this.description = description;
        this.visibility = visibility;
    }

    public void publish() {
        this.editStatus = EditStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.lastSavedAt = LocalDateTime.now();
    }

    public void markSaved() {
        this.lastSavedAt = LocalDateTime.now();
    }

    public void changeVisibility(PortfolioVisibility visibility) {
        this.visibility = visibility;
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
