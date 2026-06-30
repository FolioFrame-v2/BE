package com.folioframe.domain.portfolio.entity;

import com.folioframe.domain.common.entity.Techstack;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "project_techstack")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectTechstack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_techstack_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_project_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PortfolioProject portfolioProject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "techstack_id", nullable = false)
    private Techstack techstack;
}
