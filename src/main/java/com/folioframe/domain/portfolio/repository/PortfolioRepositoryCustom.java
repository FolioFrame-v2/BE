package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.enums.PortfolioSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PortfolioRepositoryCustom {

    // exactRegionId/provinceRegionId는 둘 다 null이거나 둘 중 하나만 채워져서 넘어온다
    // (exactRegionId: 특정 시/구/군 매칭, provinceRegionId: 시/도 전체(모든 시/구/군) 매칭)
    Page<Portfolio> findPublicPortfolios(
            String keyword,
            Long exactRegionId,
            Long provinceRegionId,
            Integer minYears,
            Integer maxYearsExclusive,
            JobRole jobRole,
            PortfolioSortType sortType,
            Pageable pageable
    );
}
