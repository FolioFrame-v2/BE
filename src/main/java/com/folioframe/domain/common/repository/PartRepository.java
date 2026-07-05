package com.folioframe.domain.common.repository;

import com.folioframe.domain.common.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {

    List<Part> findByNameContainingIgnoreCaseOrderByNameAsc(String keyword);
}
