package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.common.enums.PortfolioJobCategory;

public record PortfolioJobCategoryResDTO(
        String code,
        String label
) {
    public static PortfolioJobCategoryResDTO from(PortfolioJobCategory category) {
        return new PortfolioJobCategoryResDTO(category.name(), category.getLabel());
    }
}
