package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioFieldRepository extends JpaRepository<PortfolioField, Long> {

    List<PortfolioField> findAllByPortfolioOrderByDisplayOrder(Portfolio portfolio);
}
