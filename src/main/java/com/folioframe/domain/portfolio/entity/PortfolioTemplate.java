package com.folioframe.domain.portfolio.entity;

import com.folioframe.domain.portfolio.enums.TemplateLayoutKey;
import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "portfolio_template")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_template_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 300)
    private String description;

    // 프론트엔드가 렌더링할 실제 디자인/레이아웃 구분자(예: 미니멀/에디토리얼/터미널/플레이풀).
    // 백엔드는 콘텐츠만 다루고, 이 값을 그대로 프론트에 내려주면 프론트가 그에 맞는 디자인을 그린다.
    @Enumerated(EnumType.STRING)
    @Column(name = "layout_key", nullable = false, length = 20)
    private TemplateLayoutKey layoutKey;

    @Builder.Default
    @Column(name = "use_count", nullable = false)
    private int useCount = 0;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private boolean active = true;

    public void increaseUseCount() {
        this.useCount++;
    }
}
