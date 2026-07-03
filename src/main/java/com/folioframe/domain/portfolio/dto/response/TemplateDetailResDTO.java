package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.PortfolioTemplate;
import com.folioframe.domain.portfolio.entity.TemplateField;
import com.folioframe.domain.portfolio.enums.TemplateLayoutKey;

import java.util.List;

public record TemplateDetailResDTO(
        Long id,
        String name,
        String description,
        TemplateLayoutKey layoutKey,
        int useCount,
        List<TemplateFieldResDTO> fields
) {
    public static TemplateDetailResDTO from(PortfolioTemplate template, List<TemplateField> fields) {
        return new TemplateDetailResDTO(
                template.getId(),
                template.getName(),
                template.getDescription(),
                template.getLayoutKey(),
                template.getUseCount(),
                fields.stream().map(TemplateFieldResDTO::from).toList()
        );
    }
}
