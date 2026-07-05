package com.folioframe.domain.job.dto.response;

import com.folioframe.domain.job.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record JobApplicationResDTO(
        List<ApplicationDetail> content,
        PageInfo pageable
) {
    public record ApplicationDetail(
            Long jobApplicationId,
            String companyName,
            String positionName,
            String message,
            ApplicationStatus status,
            LocalDateTime createdAt
    ) {}

    public record PageInfo(
            int pageNumber,
            int pageSize,
            long totalElements,
            int totalPages
    ) {}
}
