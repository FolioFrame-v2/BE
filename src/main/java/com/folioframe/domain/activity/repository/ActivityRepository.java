package com.folioframe.domain.activity.repository;

import com.folioframe.domain.activity.entity.Activity;
import com.folioframe.domain.activity.enums.ActivityCategory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findAllByCategory(ActivityCategory category, Sort sort);
}
