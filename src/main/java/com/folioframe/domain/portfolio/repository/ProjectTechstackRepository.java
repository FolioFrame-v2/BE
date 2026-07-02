package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.PortfolioProject;
import com.folioframe.domain.portfolio.entity.ProjectTechstack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectTechstackRepository extends JpaRepository<ProjectTechstack, Long> {

    @Query("SELECT pt FROM ProjectTechstack pt JOIN FETCH pt.techstack WHERE pt.portfolioProject IN :portfolioProjects")
    List<ProjectTechstack> findAllByPortfolioProjectInWithTechstack(@Param("portfolioProjects") List<PortfolioProject> portfolioProjects);

    void deleteAllByPortfolioProject(PortfolioProject portfolioProject);
}
