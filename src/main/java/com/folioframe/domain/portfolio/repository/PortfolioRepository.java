package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.enums.EditStatus;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
import com.folioframe.domain.talent.entity.TalentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Page<Portfolio> findAllByTalentProfileOrderByUpdatedAtDesc(TalentProfile talentProfile, Pageable pageable);

    Page<Portfolio> findAllByVisibilityAndEditStatus(PortfolioVisibility visibility, EditStatus editStatus, Pageable pageable);

    Optional<Portfolio> findByPublicSlug(String publicSlug);
}
