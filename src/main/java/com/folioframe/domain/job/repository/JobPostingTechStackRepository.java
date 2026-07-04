package com.folioframe.domain.job.repository;

import com.folioframe.domain.job.entity.JobPostingTechstack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobPostingTechStackRepository extends JpaRepository<JobPostingTechstack, Long> {

    List<JobPostingTechstack> findByJobPostingId(Long jobPostingId);

    List<JobPostingTechstack> findByJobPostingIdIn(List<Long> jobPostingIds);

    @Modifying
    @Query("DELETE FROM JobPostingTechstack jts WHERE jts.jobPosting.id = :jobPostingId")
    void deleteByJobPostingId(@Param("jobPostingId") Long jobPostingId);
}