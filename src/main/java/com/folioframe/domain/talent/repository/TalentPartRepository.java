package com.folioframe.domain.talent.repository;

import com.folioframe.domain.talent.entity.TalentPart;
import com.folioframe.domain.talent.entity.TalentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalentPartRepository extends JpaRepository<TalentPart, Long> {

    // 특정 프로필이 가진 파트 목록 조회
    List<TalentPart> findAllByTalentProfile(TalentProfile talentProfile);

    // 특정 프로필이 가진 파트 매핑 전체 삭제 (수정 시 사용)
    void deleteAllByTalentProfile(TalentProfile talentProfile);
}
