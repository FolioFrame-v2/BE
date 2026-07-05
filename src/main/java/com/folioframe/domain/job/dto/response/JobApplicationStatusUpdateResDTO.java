package com.folioframe.domain.job.dto.response;

import com.folioframe.domain.job.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record JobApplicationStatusUpdateResDTO(
        Long jobApplicationId,
        ApplicationStatus status,
        LocalDateTime updatedAt
) {
}
