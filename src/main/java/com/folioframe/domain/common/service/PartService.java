package com.folioframe.domain.common.service;

import com.folioframe.domain.common.dto.response.PartResDTO;
import com.folioframe.domain.common.entity.Part;
import com.folioframe.domain.common.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartService {

    private final PartRepository partRepository;

    @Transactional(readOnly = true)
    public List<PartResDTO> search(String keyword) {
        List<Part> parts = (keyword == null || keyword.isBlank())
                ? partRepository.findAll()
                : partRepository.findByNameContainingIgnoreCaseOrderByNameAsc(keyword.trim());

        return parts.stream().map(PartResDTO::from).toList();
    }
}
