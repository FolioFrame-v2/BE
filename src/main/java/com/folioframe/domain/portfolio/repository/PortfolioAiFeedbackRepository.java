package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioAiFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioAiFeedbackRepository extends JpaRepository<PortfolioAiFeedback, Long> {

    Optional<PortfolioAiFeedback> findTopByPortfolioAndParentFeedbackIsNullOrderByVersionDesc(Portfolio portfolio);

    Optional<PortfolioAiFeedback> findByPortfolioAndVersionAndParentFeedbackIsNull(Portfolio portfolio, Integer version);

    Optional<PortfolioAiFeedback> findByParentFeedbackAndSubVersion(PortfolioAiFeedback parentFeedback, Integer subVersion);

    List<PortfolioAiFeedback> findAllByPortfolioAndParentFeedbackIsNullOrderByVersionAsc(Portfolio portfolio);

    List<PortfolioAiFeedback> findAllByParentFeedbackOrderBySubVersionAsc(PortfolioAiFeedback parentFeedback);

    Optional<PortfolioAiFeedback> findTopByParentFeedbackOrderBySubVersionDesc(PortfolioAiFeedback parentFeedback);
}
