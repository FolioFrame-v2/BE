package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.entity.PortfolioCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioCertificateRepository extends JpaRepository<PortfolioCertificate, Long> {

    List<PortfolioCertificate> findAllByPortfolioOrderByIssuedAtDesc(Portfolio portfolio);
}
