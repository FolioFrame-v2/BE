package com.folioframe.domain.job.repository;

import com.folioframe.domain.job.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByJobPostingId(Long jobPostingId);
}