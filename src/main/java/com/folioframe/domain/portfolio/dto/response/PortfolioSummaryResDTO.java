package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.enums.EditStatus;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;

import java.time.LocalDateTime;

public record PortfolioSummaryResDTO(
        Long id,
        String title,
        JobRole jobRole,
        String oneLiner,
        PortfolioVisibility visibility,
        EditStatus editStatus,
        int viewCount,
        int bookmarkCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PortfolioSummaryResDTO from(Portfolio portfolio) {
        return new PortfolioSummaryResDTO(
                portfolio.getId(),
                portfolio.getTitle(),
                portfolio.getJobRole(),
                portfolio.getOneLiner(),
                portfolio.getVisibility(),
                portfolio.getEditStatus(),
                portfolio.getViewCount(),
                portfolio.getBookmarkCount(),
                portfolio.getCreatedAt(),
                portfolio.getUpdatedAt()
        );
    }
}
