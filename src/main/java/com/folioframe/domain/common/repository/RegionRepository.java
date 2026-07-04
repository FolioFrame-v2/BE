package com.folioframe.domain.common.repository;

import com.folioframe.domain.common.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    // 최상위(시/도) 지역 목록 — 지역 필터/등록 1단계 드롭다운용
    List<Region> findByParentIsNullOrderByName();

    // 특정 시/도 하위의 시/군/구 목록 — 지역 필터/등록 2단계 드롭다운용
    List<Region> findByParent_IdOrderByName(Long parentId);
}
