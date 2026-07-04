package com.folioframe.domain.portfolio.repository;

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
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Page<Portfolio> findAllByTalentProfileAndConfirmedAtIsNotNullOrderByUpdatedAtDesc(TalentProfile talentProfile, Pageable pageable);

    @Query(value = """
            SELECT p FROM Portfolio p
            JOIN FETCH p.talentProfile tp
            JOIN FETCH tp.member
            JOIN FETCH tp.region r
            LEFT JOIN FETCH r.parent
            WHERE p.visibility = :visibility AND p.confirmedAt IS NOT NULL
            """,
            countQuery = "SELECT COUNT(p) FROM Portfolio p WHERE p.visibility = :visibility AND p.confirmedAt IS NOT NULL")
    Page<Portfolio> findAllByVisibilityAndConfirmedAtIsNotNull(@Param("visibility") PortfolioVisibility visibility, Pageable pageable);

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
