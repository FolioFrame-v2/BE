package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioTemplate;

public record TemplateResDTO(
        Long id,
        String name,
        String description,
        String thumbnailUrl,
        int useCount
) {
    public static TemplateResDTO from(PortfolioTemplate template) {
        return new TemplateResDTO(
                template.getId(),
                template.getName(),
                template.getDescription(),
                template.getThumbnailUrl(),
                template.getUseCount()
        );
    }
}
