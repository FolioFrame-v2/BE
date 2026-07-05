package com.folioframe.domain.job.repository;

import com.folioframe.domain.job.dto.request.JobPostingSearchCond;
import com.folioframe.domain.job.entity.JobPosting;
import org.springframework.data.domain.Page;

public interface JobPostingRepositoryCustom {

    Page<JobPosting> findByCondition(JobPostingSearchCond cond);

}