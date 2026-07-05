package com.folioframe.domain.job.repository;

import com.folioframe.domain.job.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByJobPostingId(Long jobPostingId);

    // 중복 지원 확인용
    boolean existsByTalentProfileIdAndJobPostingId(Long talentProfileId, Long jobPostingId);

    // 인재 프로필 기준으로 지원 내역 페이징 조회
    @Query("SELECT ja FROM JobApplication ja " +
            "JOIN FETCH ja.jobPosting " +
            "WHERE ja.talentProfile.id = :talentProfileId")
    Page<JobApplication> findAllByTalentProfileId(@Param("talentProfileId") Long talentProfileId, Pageable pageable);
}