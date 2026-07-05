package com.folioframe.domain.talent.repository;

import com.folioframe.domain.talent.entity.TalentCertificate;
import com.folioframe.domain.talent.entity.TalentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalentCertificateRepository extends JpaRepository<TalentCertificate, Long> {

    List<TalentCertificate> findAllByTalentProfile(TalentProfile talentProfile);
}
