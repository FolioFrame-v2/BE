package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.PortfolioAiFeedback;
import com.folioframe.domain.portfolio.entity.PortfolioAiField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioAiFieldRepository extends JpaRepository<PortfolioAiField, Long> {

    List<PortfolioAiField> findAllByFeedback(PortfolioAiFeedback feedback);
}
