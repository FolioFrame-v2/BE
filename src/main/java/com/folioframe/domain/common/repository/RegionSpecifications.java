package com.folioframe.domain.common.repository;

import com.folioframe.domain.common.entity.Region;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class RegionSpecifications {

    private RegionSpecifications() {
    }

    // 시/도(최상위) regionId가 주어지면 그 하위 리프 지역 전체를 포함하고,
    // 시/군/구(리프) regionId가 주어지면 정확히 일치하는 것만 매칭한다.
    public static Predicate matches(Join<?, Region> regionJoin, CriteriaBuilder cb, Long regionId) {
        Join<Region, Region> parentJoin = regionJoin.join("parent", JoinType.LEFT);
        return cb.or(
                cb.equal(regionJoin.get("id"), regionId),
                cb.equal(parentJoin.get("id"), regionId)
        );
    }
}
