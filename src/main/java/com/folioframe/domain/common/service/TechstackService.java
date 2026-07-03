package com.folioframe.domain.common.service;

import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.common.entity.Techstack;
import com.folioframe.domain.common.repository.TechstackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechstackService {

    private final TechstackRepository techstackRepository;

    @Transactional(readOnly = true)
    public List<TechstackResDTO> search(String keyword) {
        List<Techstack> techstacks = (keyword == null || keyword.isBlank())
                ? techstackRepository.findAll()
                : techstackRepository.findByNameContainingIgnoreCaseOrderByNameAsc(keyword.trim());

        return techstacks.stream().map(TechstackResDTO::from).toList();
    }

    @Transactional
    public TechstackResDTO findOrCreate(String rawName) {
        String name = rawName.trim();

        Techstack techstack = techstackRepository.findByNormalizedName(name)
                .orElseGet(() -> techstackRepository.save(Techstack.builder().name(name).build()));

        return TechstackResDTO.from(techstack);
    }
}
