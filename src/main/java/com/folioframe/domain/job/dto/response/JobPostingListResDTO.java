package com.folioframe.domain.job.dto.response;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.job.enums.JobPostingStatus;
import lombok.Builder;
import java.util.List;

@Builder
public record JobPostingListResDTO(
        Long jobPostingId,
        String companyName,
        CareerLevel careerLevel,
        JobPostingStatus status,
        String positionName,
        String shortDescription,
        String locationName,
        List<TechStackDto> techStacks
) {
    public record TechStackDto(
            Long techStackId,
            String name
    ) {}
}