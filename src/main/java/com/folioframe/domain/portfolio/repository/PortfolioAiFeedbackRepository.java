package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioAiFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioAiFeedbackRepository extends JpaRepository<PortfolioAiFeedback, Long> {

    Optional<PortfolioAiFeedback> findTopByPortfolioOrderByVersionDesc(Portfolio portfolio);

    int countByPortfolio(Portfolio portfolio);
}
