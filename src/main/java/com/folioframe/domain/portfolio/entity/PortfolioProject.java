package com.folioframe.domain.portfolio.entity;

import com.folioframe.domain.portfolio.enums.ProjectDuration;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "portfolio_project")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioProject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Portfolio portfolio;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "role", length = 100)
    private String role;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "project_url", length = 500)
    private String projectUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_range")
    private ProjectDuration durationRange;

    public void update(String title, String role, String content,
                       String thumbnailUrl, String projectUrl, ProjectDuration durationRange) {
        this.title = title;
        this.role = role;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.projectUrl = projectUrl;
        this.durationRange = durationRange;
    }
}
