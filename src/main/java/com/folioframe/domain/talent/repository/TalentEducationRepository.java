package com.folioframe.domain.talent.repository;

import com.folioframe.domain.talent.entity.TalentEducation;
import com.folioframe.domain.talent.entity.TalentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalentEducationRepository extends JpaRepository<TalentEducation, Long> {

    List<TalentEducation> findAllByTalentProfile(TalentProfile talentProfile);
}
