package com.folioframe.domain.company.repository;

import com.folioframe.domain.company.entity.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {

    // 특정 회원의 프로필 조회
    Optional<CompanyProfile> findByMemberId(Long memberId);

    // 사업자 등록번호 중복 확인
    boolean existsByBusinessNumber(String businessNumber);

    // 특정 회원이 이미 프로필을 등록했는지 확인
    boolean existsByMemberId(Long memberId);

    // 수정을 위한 사업자 등록번호 중복 확인 (자기 자신의 ID는 제외)
    boolean existsByBusinessNumberAndIdNot(String businessNumber, Long id);
}