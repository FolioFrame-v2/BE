package com.folioframe.domain.common.service;

import com.folioframe.domain.common.dto.response.RegionResDTO;
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
    public List<RegionResDTO> search(String keyword) {
        String normalizedKeyword = (keyword == null) ? "" : keyword.trim();

        return regionRepository.searchLeafRegions(normalizedKeyword)
                .stream()
                .map(RegionResDTO::from)
                .toList();
    }
}
