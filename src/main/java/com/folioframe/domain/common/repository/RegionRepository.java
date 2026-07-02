package com.folioframe.domain.common.repository;

import com.folioframe.domain.common.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    // 선택 가능한 지역 = 하위 지역이 없는 말단(리프) 지역 (예: "서울 강남·서초·양재", "원격")
    // 상위 지역만 있는 행("서울", "경기" 등)은 그 자체로는 선택 불가 — 항상 리프까지 선택해야 함
    @Query("""
            SELECT r FROM Region r
            LEFT JOIN r.parent p
            LEFT JOIN Region c ON c.parent = r
            WHERE c.id IS NULL
              AND LOWER(CONCAT(COALESCE(p.name, ''), ' ', r.name)) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY p.name, r.name
            """)
    List<Region> searchLeafRegions(@Param("keyword") String keyword);
}
