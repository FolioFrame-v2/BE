package com.folioframe.domain.common.repository;

import com.folioframe.domain.common.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}