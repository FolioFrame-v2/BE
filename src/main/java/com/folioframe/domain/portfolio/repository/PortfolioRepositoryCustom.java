package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.enums.PortfolioSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface PortfolioRepositoryCustom {

    // jobRoles는 항상 비어있지 않은 값으로 넘어온다(카테고리 미선택 시 JobRole.values() 전체,
    // 선택 시 해당 카테고리에 속한 JobRole 집합)
    Page<Portfolio> findPublicPortfolios(
            String keyword,
            Long regionId,
            Integer minYears,
            Integer maxYearsExclusive,
            Collection<JobRole> jobRoles,
            PortfolioSortType sortType,
            Pageable pageable
    );
}
