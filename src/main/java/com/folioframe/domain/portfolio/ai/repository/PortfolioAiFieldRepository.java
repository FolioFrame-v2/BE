package com.folioframe.domain.portfolio.ai.repository;

import com.folioframe.domain.portfolio.ai.entity.PortfolioAiFeedback;
import com.folioframe.domain.portfolio.ai.entity.PortfolioAiField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioAiFieldRepository extends JpaRepository<PortfolioAiField, Long> {

    List<PortfolioAiField> findAllByFeedback(PortfolioAiFeedback feedback);

    List<PortfolioAiField> findAllByFeedbackIn(List<PortfolioAiFeedback> feedbacks);
}
