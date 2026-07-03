package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioTemplate;
import com.folioframe.domain.portfolio.enums.TemplateLayoutKey;

public record TemplateResDTO(
        Long id,
        String name,
        String description,
        TemplateLayoutKey layoutKey,
        int useCount
) {
    public static TemplateResDTO from(PortfolioTemplate template) {
        return new TemplateResDTO(
                template.getId(),
                template.getName(),
                template.getDescription(),
                template.getLayoutKey(),
                template.getUseCount()
        );
    }
}
