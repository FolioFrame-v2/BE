package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.PortfolioTemplate;
import com.folioframe.domain.portfolio.entity.TemplateField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateFieldRepository extends JpaRepository<TemplateField, Long> {

    List<TemplateField> findAllByTemplateOrderByDisplayOrder(PortfolioTemplate template);
}
