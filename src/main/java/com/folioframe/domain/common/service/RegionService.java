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
}
