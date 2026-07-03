package com.folioframe.domain.common.dto.response;

import com.folioframe.domain.common.entity.Techstack;

public record TechstackResDTO(
        Long id,
        String name
) {
    public static TechstackResDTO from(Techstack techstack) {
        return new TechstackResDTO(techstack.getId(), techstack.getName());
    }
}
