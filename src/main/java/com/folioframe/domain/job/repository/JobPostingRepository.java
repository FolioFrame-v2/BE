package com.folioframe.domain.job.repository;

import com.folioframe.domain.job.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long>, JobPostingRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE JobPosting j SET j.viewCount = j.viewCount + 1 WHERE j.id = :jobPostingId")
    void incrementViewCount(@Param("jobPostingId") Long jobPostingId);
}