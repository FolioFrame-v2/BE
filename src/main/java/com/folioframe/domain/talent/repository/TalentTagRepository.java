package com.folioframe.domain.talent.repository;

import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.domain.talent.entity.TalentTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalentTagRepository extends JpaRepository<TalentTag, Long> {

    List<TalentTag> findAllByTalentProfile(TalentProfile talentProfile);

    void deleteAllByTalentProfile(TalentProfile talentProfile);
}