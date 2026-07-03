package com.folioframe.domain.portfolio.entity;

import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

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

    @Column(name = "project_url", length = 500)
    private String projectUrl;

    // 연-월 단위로만 사용 (일자는 매월 1일로 고정)
    @Column(name = "started_at")
    private LocalDate startedAt;

    @Column(name = "ended_at")
    private LocalDate endedAt;

    public void update(String title, String role, String content,
                       String projectUrl,
                       LocalDate startedAt, LocalDate endedAt) {
        this.title = title;
        this.role = role;
        this.content = content;
        this.projectUrl = projectUrl;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
