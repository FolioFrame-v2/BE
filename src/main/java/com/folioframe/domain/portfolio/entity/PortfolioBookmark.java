package com.folioframe.domain.portfolio.entity;

import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(
        name = "portfolio_bookmark",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_portfolio_bookmark_member_portfolio",
                columnNames = {"member_id", "portfolio_id"}
        )
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Portfolio portfolio;

    @Column(name = "member_id", nullable = false)
    private Long memberId;
}
