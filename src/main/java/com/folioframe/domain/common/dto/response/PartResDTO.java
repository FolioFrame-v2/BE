package com.folioframe.domain.common.dto.response;

import com.folioframe.domain.common.entity.Part;

public record PartResDTO(
        Long id,
        String name
) {
    public static PartResDTO from(Part part) {
        return new PartResDTO(part.getId(), part.getName());
    }
}
