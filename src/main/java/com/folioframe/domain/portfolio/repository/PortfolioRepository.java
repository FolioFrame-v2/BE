package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
import com.folioframe.domain.talent.entity.TalentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Page<Portfolio> findAllByTalentProfileAndConfirmedAtIsNotNullOrderByLastSavedAtDesc(TalentProfile talentProfile, Pageable pageable);

    // jobRoles는 항상 비어있지 않은 값으로 넘어온다(카테고리 미선택 시 JobRole.values() 전체,
    // 선택 시 해당 카테고리에 속한 JobRole 집합) — 그래야 컬렉션 파라미터의 IN 절 null 처리를
    // JPQL에서 신경 쓸 필요 없이 항상 같은 조건으로 필터링할 수 있다.
    @Query(value = """
            SELECT p FROM Portfolio p
            JOIN FETCH p.talentProfile tp
            JOIN FETCH tp.member
            JOIN FETCH tp.region r
            LEFT JOIN FETCH r.parent
            WHERE p.visibility = :visibility AND p.confirmedAt IS NOT NULL
              AND (:minYears IS NULL OR tp.careerYears >= :minYears)
              AND (:maxYearsExclusive IS NULL OR tp.careerYears < :maxYearsExclusive)
              AND p.jobRole IN :jobRoles
            """,
            countQuery = """
            SELECT COUNT(p) FROM Portfolio p JOIN p.talentProfile tp
            WHERE p.visibility = :visibility AND p.confirmedAt IS NOT NULL
              AND (:minYears IS NULL OR tp.careerYears >= :minYears)
              AND (:maxYearsExclusive IS NULL OR tp.careerYears < :maxYearsExclusive)
              AND p.jobRole IN :jobRoles
            """)
    Page<Portfolio> findAllByVisibilityAndConfirmedAtIsNotNull(
            @Param("visibility") PortfolioVisibility visibility,
            @Param("minYears") Integer minYears,
            @Param("maxYearsExclusive") Integer maxYearsExclusive,
            @Param("jobRoles") Collection<JobRole> jobRoles,
            Pageable pageable);

    Optional<Portfolio> findByPublicSlug(String publicSlug);

    // 방치 정리 스케줄러 전용. cutoff보다 오래 전에 생성됐지만 한 번도 확정(저장/AI첨삭/게시하기)되지
    // 않은 초안을 지운다. 공개/비공개만 선택하고 게시하기를 누르지 않은 채 나간 경우도 확정된 것이
    // 아니므로(Portfolio.changeVisibility는 confirmSave를 호출하지 않는다) visibility와 무관하게
    // confirmedAt만으로 판단한다. 자식 테이블은 이미 DB에 세팅된 ON DELETE CASCADE가 정리하므로
    // 엔티티를 로드할 필요 없이 벌크 삭제 한 번으로 처리한다.
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Portfolio p WHERE p.confirmedAt IS NULL AND p.createdAt < :cutoff")
    int deleteAbandonedDrafts(@Param("cutoff") LocalDateTime cutoff);
}
