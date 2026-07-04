package com.folioframe.domain.talent.repository;

import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.domain.talent.entity.TalentTechstack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalentTechstackRepository extends JpaRepository<TalentTechstack, Long> {

    // 특정 프로필이 가진 기술스택 목록 조회
    List<TalentTechstack> findAllByTalentProfile(TalentProfile talentProfile);

    // 특정 프로필이 가진 기술스택 매핑 전체 삭제 (수정 시 사용)
    void deleteAllByTalentProfile(TalentProfile talentProfile);
}