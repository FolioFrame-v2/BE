package com.folioframe.domain.job.dto.response;

import com.folioframe.domain.job.enums.ApplicationStatus;

public record JobApplicationCreateResDTO(
        Long jobApplicationId,
        ApplicationStatus status
) {
}
