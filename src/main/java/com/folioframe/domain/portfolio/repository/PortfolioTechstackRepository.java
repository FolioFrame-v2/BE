package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioTechstack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioTechstackRepository extends JpaRepository<PortfolioTechstack, Long> {

    @Query("SELECT pt FROM PortfolioTechstack pt JOIN FETCH pt.techstack WHERE pt.portfolio = :portfolio")
    List<PortfolioTechstack> findAllByPortfolioWithTechstack(@Param("portfolio") Portfolio portfolio);

    void deleteAllByPortfolio(Portfolio portfolio);
}
