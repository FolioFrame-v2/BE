package com.folioframe.domain.talent.repository;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.job.enums.EmploymentType;
import com.folioframe.domain.talent.dto.response.TalentProfileSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TalentProfileRepositoryCustom {

    Page<TalentProfileSimpleResponse> searchDynamic(
            String sort,
            CareerLevel career,
            EmploymentType employment,
            String techStack,
            JobRole job,
            Pageable pageable
    );
}