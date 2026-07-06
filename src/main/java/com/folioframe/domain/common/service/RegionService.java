package com.folioframe.domain.common.service;

import com.folioframe.domain.common.dto.response.RegionResDTO;
import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.common.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    @Transactional(readOnly = true)
    public List<RegionResDTO> getRegions(Long parentId) {
        List<Region> regions = (parentId != null)
                ? regionRepository.findByParent_IdOrderByName(parentId)
                : regionRepository.findByParentIsNullOrderByName();

        return regions.stream()
                .map(RegionResDTO::from)
                .toList();
    }

    // 시/도 ID가 넘어오면 그 시/도 전체(모든 시/구/군)를, 시/구/군의 "전체" 항목이 넘어와도 같은 시/도 전체를 매칭시키고,
    // 그 외 특정 시/구/군이면 정확히 그 지역만 매칭시킨다. regionId가 지역 테이블에 없으면(잘못된 값) 있는 그대로
    // 정확매칭에 넘겨 결과가 0건이 되도록 한다.
    @Transactional(readOnly = true)
    public RegionFilter resolveRegionFilter(Long regionId) {
        if (regionId == null) return new RegionFilter(null, null);

        return regionRepository.findById(regionId)
                .map(region -> {
                    if (region.getParent() == null) {
                        return new RegionFilter(null, region.getId());
                    }
                    if ("전체".equals(region.getName())) {
                        return new RegionFilter(null, region.getParent().getId());
                    }
                    return new RegionFilter(region.getId(), null);
                })
                .orElse(new RegionFilter(regionId, null));
    }

    // exactRegionId: 특정 시/구/군 정확 매칭. provinceRegionId: 시/도 전체(그 아래 모든 시/구/군) 매칭
    public record RegionFilter(Long exactRegionId, Long provinceRegionId) {}
}
