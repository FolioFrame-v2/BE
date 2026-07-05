package com.folioframe.domain.company.repository;

import com.folioframe.domain.company.entity.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {

    // 특정 회원의 프로필 조회
    Optional<CompanyProfile> findByMemberId(Long memberId);

    // 특정 회원이 이미 프로필을 등록했는지 확인
    boolean existsByMemberId(Long memberId);
}