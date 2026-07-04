package com.folioframe.domain.job.repository;

import com.folioframe.domain.job.entity.JobPosting;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.folioframe.domain.company.entity.QCompanyProfile.companyProfile;
import static com.folioframe.domain.job.entity.QJobPosting.jobPosting;

@Repository
@RequiredArgsConstructor
public class JobPostingRepositoryImpl implements JobPostingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<JobPosting> findByCondition(String keyword, Long regionId, Pageable pageable) {

        // 1. 데이터 조회 쿼리 (페이징 적용)
        List<JobPosting> content = queryFactory
                .selectFrom(jobPosting)
                .leftJoin(jobPosting.companyProfile, companyProfile).fetchJoin()
                .where(
                        containsKeyword(keyword),
                        eqRegionId(regionId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(jobPosting.createdAt.desc())
                .fetch();

        // 2. 카운트 쿼리 (페이징 처리를 위해 전체 개수 계산)
        JPAQuery<Long> countQuery = queryFactory
                .select(jobPosting.count())
                .from(jobPosting)
                .leftJoin(jobPosting.companyProfile, companyProfile)
                .where(
                        containsKeyword(keyword),
                        eqRegionId(regionId)
                );

        // 3. Page 객체로 반환
        return new PageImpl<>(content, pageable, countQuery.fetchOne() != null ? countQuery.fetchOne() : 0L);
    }

    // 동적 쿼리: 키워드 검색 (공고명 또는 기업명)
    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return jobPosting.positionName.containsIgnoreCase(keyword)
                .or(companyProfile.companyName.containsIgnoreCase(keyword));
    }

    // 동적 쿼리: 지역 ID 일치 여부
    private BooleanExpression eqRegionId(Long regionId) {
        if (regionId == null) {
            return null;
        }
        return jobPosting.region.id.eq(regionId);
    }
}