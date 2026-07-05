package com.folioframe.domain.talent.repository;

import com.folioframe.domain.talent.entity.TalentCareer;
import com.folioframe.domain.talent.entity.TalentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalentCareerRepository extends JpaRepository<TalentCareer, Long> {

    List<TalentCareer> findAllByTalentProfileOrderByStartedAtDesc(TalentProfile talentProfile);
}
