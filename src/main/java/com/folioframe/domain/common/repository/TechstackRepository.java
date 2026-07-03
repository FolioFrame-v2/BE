package com.folioframe.domain.common.repository;

import com.folioframe.domain.common.entity.Techstack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TechstackRepository extends JpaRepository<Techstack, Long> {

    List<Techstack> findAllByIdIn(List<Long> ids);

    List<Techstack> findByNameContainingIgnoreCaseOrderByNameAsc(String keyword);

    // 대소문자·공백 차이(예: "Spring", "spring", "java script")를 같은 기술스택으로 취급
    @Query("SELECT t FROM Techstack t WHERE REPLACE(LOWER(t.name), ' ', '') = REPLACE(LOWER(:name), ' ', '')")
    Optional<Techstack> findByNormalizedName(@Param("name") String name);
}
