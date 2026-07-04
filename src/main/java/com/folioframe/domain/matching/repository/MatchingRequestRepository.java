package com.folioframe.domain.matching.repository;

import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.matching.entity.MatchingRequest;
import com.folioframe.domain.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long> {
    boolean existsByCompanyProfileAndPortfolio(CompanyProfile companyProfile, Portfolio portfolio);
}