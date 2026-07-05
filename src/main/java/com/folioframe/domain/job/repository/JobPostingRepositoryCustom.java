package com.folioframe.domain.job.repository;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.job.entity.JobPosting;
import com.folioframe.domain.job.enums.JobPostingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostingRepositoryCustom {
    Page<JobPosting> findByCondition(
            String keyword,
            Long regionId,
            CareerLevel careerLevel,
            JobPostingStatus status,
            String sort,
            Pageable pageable
    );
}