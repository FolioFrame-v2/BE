package com.folioframe.domain.activity.repository;

import com.folioframe.domain.activity.entity.Activity;
import com.folioframe.domain.activity.enums.ActivityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Page<Activity> findAllByCategory(ActivityCategory category, Pageable pageable);
}
