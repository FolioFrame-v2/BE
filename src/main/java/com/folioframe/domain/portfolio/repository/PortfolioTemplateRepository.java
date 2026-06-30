package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.PortfolioTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioTemplateRepository extends JpaRepository<PortfolioTemplate, Long> {

    List<PortfolioTemplate> findAllByActiveTrueOrderByUseCountDesc();
}
