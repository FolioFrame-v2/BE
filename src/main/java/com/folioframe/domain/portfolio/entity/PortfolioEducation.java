package com.folioframe.domain.portfolio.entity;

import com.folioframe.domain.portfolio.enums.DegreeType;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "portfolio_education")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioEducation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_education_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Portfolio portfolio;

    @Column(name = "school_name", nullable = false, length = 100)
    private String schoolName;

    @Column(name = "major", length = 100)
    private String major;

    @Enumerated(EnumType.STRING)
    @Column(name = "degree")
    private DegreeType degree;

    @Column(name = "started_at")
    private LocalDate startedAt;

    // null 이면 재학중
    @Column(name = "ended_at")
    private LocalDate endedAt;

    // false 이면 재학중·중퇴
    @Builder.Default
    @Column(name = "graduated", nullable = false)
    private boolean graduated = true;

    public void update(String schoolName, String major, DegreeType degree,
                       LocalDate startedAt, LocalDate endedAt, boolean graduated) {
        this.schoolName = schoolName;
        this.major = major;
        this.degree = degree;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.graduated = graduated;
    }
}
