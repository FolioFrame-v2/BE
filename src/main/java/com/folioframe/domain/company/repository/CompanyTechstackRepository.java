package com.folioframe.domain.company.repository;

import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.company.entity.CompanyTechstack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyTechstackRepository extends JpaRepository<CompanyTechstack, Long> {

    // 특정 기업 프로필이 요구하는 기술스택 목록 조회
    List<CompanyTechstack> findAllByCompanyProfile(CompanyProfile companyProfile);

    // 특정 기업 프로필이 가진 기술스택 매핑 전체 삭제 (수정 시 사용)
    void deleteAllByCompanyProfile(CompanyProfile companyProfile);
}
