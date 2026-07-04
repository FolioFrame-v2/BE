package com.folioframe.domain.job.repository;

import com.folioframe.domain.job.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostingRepositoryCustom {
    Page<JobPosting> findByCondition(String keyword, Long regionId, Pageable pageable);
}