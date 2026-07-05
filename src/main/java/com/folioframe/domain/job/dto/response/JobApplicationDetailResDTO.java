package com.folioframe.domain.job.dto.response;

import com.folioframe.domain.job.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record JobApplicationDetailResDTO(
        Long jobApplicationId,
        Long jobPostingId,
        Long portfolioId,
        String message,
        ApplicationStatus status,
        LocalDateTime createdAt
) {
}
