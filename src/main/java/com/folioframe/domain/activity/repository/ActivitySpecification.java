package com.folioframe.domain.activity.repository;

import com.folioframe.domain.activity.entity.Activity;
import com.folioframe.domain.activity.enums.ActivityCategory;
import com.folioframe.domain.activity.enums.ActivityField;
import com.folioframe.domain.activity.enums.ActivityTeamSize;
import com.folioframe.domain.common.entity.Region;
import com.folioframe.domain.common.repository.RegionSpecifications;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ActivitySpecification {

    private ActivitySpecification() {
    }

    public static Specification<Activity> search(String keyword, ActivityCategory category, Long regionId,
                                                   ActivityField field, ActivityTeamSize teamSize) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("organizer")), pattern)
                ));
            }
            if (category != null) {
                predicate = cb.and(predicate, cb.equal(root.get("category"), category));
            }
            if (regionId != null) {
                Join<Activity, Region> regionJoin = root.join("region", JoinType.LEFT);
                predicate = cb.and(predicate, RegionSpecifications.matches(regionJoin, cb, regionId));
            }
            if (field != null) {
                predicate = cb.and(predicate, cb.equal(root.get("field"), field));
            }
            if (teamSize != null) {
                predicate = cb.and(predicate, cb.equal(root.get("teamSize"), teamSize));
            }

            return predicate;
        };
    }
}
