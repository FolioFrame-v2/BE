package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioBookmarkRepository extends JpaRepository<PortfolioBookmark, Long> {

    boolean existsByMemberIdAndPortfolio(Long memberId, Portfolio portfolio);

    Optional<PortfolioBookmark> findByMemberIdAndPortfolio(Long memberId, Portfolio portfolio);
}
