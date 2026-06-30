package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioCareer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioCareerRepository extends JpaRepository<PortfolioCareer, Long> {

    List<PortfolioCareer> findAllByPortfolioOrderByStartedAtDesc(Portfolio portfolio);
}
