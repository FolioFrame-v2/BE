package com.folioframe.domain.job.repository;

import com.folioframe.domain.job.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long>, JobPostingRepositoryCustom {
}