package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.PortfolioTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioTemplateRepository extends JpaRepository<PortfolioTemplate, Long> {

    Page<PortfolioTemplate> findAllByActiveTrueOrderByUseCountDesc(Pageable pageable);
}
