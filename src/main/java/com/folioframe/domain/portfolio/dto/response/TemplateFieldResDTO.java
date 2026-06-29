package com.folioframe.domain.portfolio.dto.response;

import com.folioframe.domain.portfolio.entity.TemplateField;

public record TemplateFieldResDTO(
        Long id,
        String title,
        String description,
        int displayOrder,
        boolean required
) {
    public static TemplateFieldResDTO from(TemplateField field) {
        return new TemplateFieldResDTO(
                field.getId(),
                field.getTitle(),
                field.getDescription(),
                field.getDisplayOrder(),
                field.isRequired()
        );
    }
}
