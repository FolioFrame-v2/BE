package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioProjectRepository extends JpaRepository<PortfolioProject, Long> {

    List<PortfolioProject> findAllByPortfolioOrderByCreatedAtDesc(Portfolio portfolio);
}
