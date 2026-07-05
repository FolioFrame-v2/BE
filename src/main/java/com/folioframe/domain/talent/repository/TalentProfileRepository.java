package com.folioframe.domain.talent.repository;

import com.folioframe.domain.talent.entity.TalentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TalentProfileRepository extends JpaRepository<TalentProfile, Long> {

    Optional<TalentProfile> findByMemberId(Long memberId);
}
