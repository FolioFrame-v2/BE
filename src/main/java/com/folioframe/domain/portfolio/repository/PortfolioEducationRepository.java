package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioEducationRepository extends JpaRepository<PortfolioEducation, Long> {

    List<PortfolioEducation> findAllByPortfolioOrderByStartedAtDesc(Portfolio portfolio);
}
