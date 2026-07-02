package com.folioframe.domain.common.dto.response;

import com.folioframe.domain.common.entity.Region;

public record RegionResDTO(
        Long id,
        String name,
        String parentName,
        String fullName
) {
    public static RegionResDTO from(Region region) {
        String parentName = region.getParent() != null ? region.getParent().getName() : null;
        String fullName = parentName != null ? parentName + " " + region.getName() : region.getName();
        return new RegionResDTO(region.getId(), region.getName(), parentName, fullName);
    }
}
