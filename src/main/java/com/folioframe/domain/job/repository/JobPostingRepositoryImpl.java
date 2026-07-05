package com.folioframe.domain.job.repository;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.job.entity.JobPosting;
import com.folioframe.domain.job.enums.JobPostingStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static com.folioframe.domain.company.entity.QCompanyProfile.companyProfile;
import static com.folioframe.domain.job.entity.QJobPosting.jobPosting;
import static com.folioframe.domain.job.entity.QJobPostingTechstack.jobPostingTechstack;
import static com.folioframe.domain.common.entity.QTechstack.techstack;

@Repository
@RequiredArgsConstructor
public class JobPostingRepositoryImpl implements JobPostingRepositoryCustom {

    private static final int CLOSING_SOON_THRESHOLD_DAYS = 7;

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<JobPosting> findByCondition(
            String keyword, Long regionId, CareerLevel careerLevel, JobPostingStatus status,
            String sort, Pageable pageable) {

        // 1. 데이터 조회 쿼리 (페이징 적용)
        List<JobPosting> content = queryFactory
                .selectFrom(jobPosting)
                .leftJoin(jobPosting.companyProfile, companyProfile).fetchJoin()
                .where(
                        matchesKeyword(keyword),
                        eqRegionId(regionId),
                        eqCareerLevel(careerLevel),
                        eqStatus(status)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSortOrder(sort))
                .fetch();

        // 2. 카운트 쿼리 (페이징 처리를 위해 전체 개수 계산)
        JPAQuery<Long> countQuery = queryFactory
                .select(jobPosting.count())
                .from(jobPosting)
                .leftJoin(jobPosting.companyProfile, companyProfile)
                .where(
                        matchesKeyword(keyword),
                        eqRegionId(regionId),
                        eqCareerLevel(careerLevel),
                        eqStatus(status)
                );

        return new PageImpl<>(content, pageable, countQuery.fetchOne() != null ? countQuery.fetchOne() : 0L);
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
        if (!matchedRoles.isEmpty()) {
            matched = matched.or(jobPosting.jobRole.in(matchedRoles));
        }

        return matched;
    }

    // 동적 쿼리: 지역 ID 일치 여부
    private BooleanExpression eqRegionId(Long regionId) {
        if (regionId == null) return null;
        return jobPosting.region.id.eq(regionId);
    }

    // 동적 쿼리: 희망 경력 버킷 일치 여부
    private BooleanExpression eqCareerLevel(CareerLevel careerLevel) {
        if (careerLevel == null) return null;
        return jobPosting.careerLevel.eq(careerLevel);
    }

    // 동적 쿼리: 상태(전체/채용중/마감임박/마감) — 마감임박·마감은 저장된 값이 아니라 deadline 기준으로 파생 판정
    private BooleanExpression eqStatus(JobPostingStatus status) {
        if (status == null) return null;

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

    private OrderSpecifier<?> getSortOrder(String sort) {
        if (!StringUtils.hasText(sort)) {
            return jobPosting.createdAt.desc();
        }

        return switch (sort.toUpperCase()) {
            case "POPULAR" -> jobPosting.bookmarkCount.desc();
            case "MOST_VIEWED" -> jobPosting.viewCount.desc();
            default -> jobPosting.createdAt.desc();
        };
    }
}
