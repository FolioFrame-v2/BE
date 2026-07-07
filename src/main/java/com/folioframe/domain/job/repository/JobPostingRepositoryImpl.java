package com.folioframe.domain.job.repository;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.job.dto.request.JobPostingSearchCond;
import com.folioframe.domain.job.entity.JobPosting;
import com.folioframe.domain.job.enums.JobPostingSort;
import com.folioframe.domain.job.enums.JobPostingStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static com.folioframe.domain.common.entity.QTechstack.techstack;
import static com.folioframe.domain.company.entity.QCompanyProfile.companyProfile;
import static com.folioframe.domain.job.entity.QJobPosting.jobPosting;
import static com.folioframe.domain.job.entity.QJobPostingTechstack.jobPostingTechstack;

@Repository
@RequiredArgsConstructor
public class JobPostingRepositoryImpl implements JobPostingRepositoryCustom {

    private static final int CLOSING_SOON_THRESHOLD_DAYS = 7;

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<JobPosting> findByCondition(JobPostingSearchCond cond, Long exactRegionId, Long provinceRegionId) {

        // 1. 커스텀 페이징 계산 (UI는 1페이지부터, DB offset은 0부터)
        int page = Math.max(1, cond.getPage() != null ? cond.getPage() : 1);
        int size = cond.getSize() != null ? cond.getSize() : 9;
        long offset = (long) (page - 1) * size;

        // 2. 데이터 조회 쿼리
        List<JobPosting> content = queryFactory
                .selectFrom(jobPosting).distinct()
                .leftJoin(jobPosting.companyProfile, companyProfile).fetchJoin()
                .leftJoin(jobPostingTechstack).on(jobPostingTechstack.jobPosting.eq(jobPosting))
                .leftJoin(jobPostingTechstack.techstack, techstack)
                .where(
                        matchesKeyword(cond.getKeyword()),
                        eqRegionId(exactRegionId, provinceRegionId),
                        eqCareerLevel(cond.getCareerLevel()),
                        eqStatus(cond.getStatus())
                )
                .offset(offset)
                .limit(size)
                .orderBy(jobPostingSort(cond.getSort()))
                .fetch();

        // 3. 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(jobPosting.countDistinct())
                .from(jobPosting)
                .leftJoin(jobPosting.companyProfile, companyProfile)
                .leftJoin(jobPostingTechstack).on(jobPostingTechstack.jobPosting.eq(jobPosting))
                .leftJoin(jobPostingTechstack.techstack, techstack)
                .where(
                        matchesKeyword(cond.getKeyword()),
                        eqRegionId(exactRegionId, provinceRegionId),
                        eqCareerLevel(cond.getCareerLevel()),
                        eqStatus(cond.getStatus())
                );

        // 4. PageRequest 객체를 만들어 PageImpl 로 반환
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return new PageImpl<>(content, pageRequest, countQuery.fetchOne() != null ? countQuery.fetchOne() : 0L);
    }

    // 동적 쿼리: 커스텀 동적 정렬
    private OrderSpecifier<?> jobPostingSort(JobPostingSort sort) {
        if (sort == null) {
            return jobPosting.createdAt.desc(); // 기본값: 최신순
        }
        return switch (sort) {
            case POPULAR -> jobPosting.bookmarkCount.desc(); // 인기순
            case MOST_VIEWED -> jobPosting.viewCount.desc(); // 조회순
            case LATEST -> jobPosting.createdAt.desc();      // 최신순
        };
    }

    // 동적 쿼리: 통합 검색 — 기업명 / 직무(라벨·이름 일치) / 요구 기술스택 중 하나라도 겹치면 매칭(OR)
    private BooleanExpression matchesKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;

        BooleanExpression byCompanyName = companyProfile.companyName.containsIgnoreCase(keyword);

        BooleanExpression byTechStack = jobPosting.id.in(
                JPAExpressions.select(jobPostingTechstack.jobPosting.id)
                        .from(jobPostingTechstack)
                        .join(jobPostingTechstack.techstack, techstack)
                        .where(techstack.name.containsIgnoreCase(keyword))
        );

        BooleanExpression matched = byCompanyName.or(byTechStack);

        List<JobRole> matchedRoles = JobRole.findByKeyword(keyword);
        if (matchedRoles != null && !matchedRoles.isEmpty()) {
            matched = matched.or(jobPosting.jobRole.in(matchedRoles));
        }

        return matched;
    }

    // 동적 쿼리: 지역 필터. exactRegionId: 특정 시/구/군 정확 매칭. provinceRegionId: 시/도 전체(그 아래 모든 시/구/군) 매칭
    private BooleanExpression eqRegionId(Long exactRegionId, Long provinceRegionId) {
        if (exactRegionId != null) return jobPosting.region.id.eq(exactRegionId);
        if (provinceRegionId != null) return jobPosting.region.parent.id.eq(provinceRegionId);
        return null;
    }

    // 동적 쿼리: 상태(전체/채용중/마감임박/마감) — 마감임박·마감은 저장된 값이 아니라 deadline 기준으로 파생 판정
    private BooleanExpression eqStatus(JobPostingStatus status) {
        if (status == null || status.name().equals("ALL")) return null;

        LocalDate today = LocalDate.now();
        LocalDate closingSoonBoundary = today.plusDays(CLOSING_SOON_THRESHOLD_DAYS);

        return switch (status) {
            case CLOSED -> jobPosting.status.eq(JobPostingStatus.CLOSED)
                    .or(jobPosting.deadline.lt(today));
            case CLOSING_SOON -> jobPosting.status.ne(JobPostingStatus.CLOSED)
                    .and(jobPosting.deadline.goe(today))
                    .and(jobPosting.deadline.loe(closingSoonBoundary));
            case ACTIVE -> jobPosting.status.ne(JobPostingStatus.CLOSED)
                    .and(jobPosting.deadline.gt(closingSoonBoundary));
        };
    }

    // 동적 쿼리: 희망 경력 버킷 일치 여부
    private BooleanExpression eqCareerLevel(CareerLevel careerLevel) {
        if (careerLevel == null) return null;
        return jobPosting.careerLevel.eq(careerLevel);
    }
}