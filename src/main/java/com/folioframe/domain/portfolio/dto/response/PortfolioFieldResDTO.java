package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioField;

public record PortfolioFieldResDTO(
        Long id,
        Long portfolioId,
        String title,
        String description,
        String content,
        int displayOrder
) {
    public static PortfolioFieldResDTO from(PortfolioField field) {
        return new PortfolioFieldResDTO(
                field.getId(),
                field.getPortfolio().getId(),
                field.getTitle(),
                field.getDescription(),
                field.getContent(),
                field.getDisplayOrder()
        );
    }
}
