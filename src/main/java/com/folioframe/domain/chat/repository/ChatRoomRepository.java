package com.folioframe.domain.chat.repository;

import com.folioframe.domain.chat.entity.ChatRoom;
import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.job.entity.JobPosting;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.talent.entity.TalentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryCustom {

    Optional<ChatRoom> findByCompanyProfileAndTalentProfileAndPortfolio(
            CompanyProfile companyProfile, TalentProfile talentProfile, Portfolio portfolio);

    Optional<ChatRoom> findByCompanyProfileAndTalentProfileAndJobPosting(
            CompanyProfile companyProfile, TalentProfile talentProfile, JobPosting jobPosting);
}
