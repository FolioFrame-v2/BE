package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;

import java.time.LocalDateTime;

public record PortfolioMyListResDTO(
        Long id,
        String title,
        LocalDateTime updatedAt,
        int viewCount,
        PortfolioVisibility visibility
) {
    public static PortfolioMyListResDTO from(Portfolio portfolio) {
        return new PortfolioMyListResDTO(
                portfolio.getId(),
                portfolio.getTitle(),
                portfolio.getUpdatedAt(),
                portfolio.getViewCount(),
                portfolio.getVisibility()
        );
    }
}
