package com.folioframe.domain.company.repository;

import com.folioframe.domain.company.entity.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
    boolean existsByBusinessNumber(String businessNumber);
    Optional<CompanyProfile> findByMemberId(Long memberId);
}
