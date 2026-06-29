package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.talent.entity.TalentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findAllByTalentProfileOrderByCreatedAtDesc(TalentProfile talentProfile);

    Optional<Portfolio> findByPublicSlug(String publicSlug);
}
