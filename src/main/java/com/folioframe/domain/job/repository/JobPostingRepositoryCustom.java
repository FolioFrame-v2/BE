package com.folioframe.domain.job.repository;

import com.folioframe.domain.job.dto.request.JobPostingSearchCond;
import com.folioframe.domain.job.entity.JobPosting;
import org.springframework.data.domain.Page;

public interface JobPostingRepositoryCustom {

    // exactRegionId/provinceRegionId는 둘 다 null이거나 둘 중 하나만 채워져서 넘어온다
    // (exactRegionId: 특정 시/구/군 매칭, provinceRegionId: 시/도 전체(모든 시/구/군) 매칭)
    Page<JobPosting> findByCondition(JobPostingSearchCond cond, Long exactRegionId, Long provinceRegionId);

}