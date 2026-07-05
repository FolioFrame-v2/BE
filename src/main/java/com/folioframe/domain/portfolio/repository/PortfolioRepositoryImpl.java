package com.folioframe.domain.portfolio.repository;

import com.folioframe.domain.common.entity.QRegion;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.enums.PortfolioSortType;
import com.folioframe.domain.portfolio.enums.PortfolioVisibility;
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

import java.util.Collection;
import java.util.List;

import static com.folioframe.domain.portfolio.entity.QPortfolio.portfolio;
import static com.folioframe.domain.talent.entity.QTalentProfile.talentProfile;
import static com.folioframe.domain.member.entity.QMember.member;
import static com.folioframe.domain.common.entity.QRegion.region;
import static com.folioframe.domain.portfolio.entity.QPortfolioTechstack.portfolioTechstack;
import static com.folioframe.domain.common.entity.QTechstack.techstack;

@Repository
@RequiredArgsConstructor
public class PortfolioRepositoryImpl implements PortfolioRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Portfolio> findPublicPortfolios(
            String keyword, Long regionId, Integer minYears, Integer maxYearsExclusive,
            Collection<JobRole> jobRoles, PortfolioSortType sortType, Pageable pageable) {

        QRegion parentRegion = new QRegion("parentRegion");

        List<Portfolio> content = queryFactory
                .selectFrom(portfolio)
                .join(portfolio.talentProfile, talentProfile).fetchJoin()
                .join(talentProfile.member, member).fetchJoin()
                .join(talentProfile.region, region).fetchJoin()
                .leftJoin(region.parent, parentRegion).fetchJoin()
                .where(
                        portfolio.visibility.eq(PortfolioVisibility.PUBLIC),
                        portfolio.confirmedAt.isNotNull(),
                        goeCareerYears(minYears),
                        ltCareerYears(maxYearsExclusive),
                        portfolio.jobRole.in(jobRoles),
                        eqRegionId(regionId),
                        matchesKeyword(keyword)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSortOrder(sortType))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(portfolio.count())
                .from(portfolio)
                .join(portfolio.talentProfile, talentProfile)
                .join(talentProfile.member, member)
                .where(
                        portfolio.visibility.eq(PortfolioVisibility.PUBLIC),
                        portfolio.confirmedAt.isNotNull(),
                        goeCareerYears(minYears),
                        ltCareerYears(maxYearsExclusive),
                        portfolio.jobRole.in(jobRoles),
                        eqRegionId(regionId),
                        matchesKeyword(keyword)
                );

        Long total = countQuery.fetchOne();
        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression goeCareerYears(Integer minYears) {
        return minYears != null ? talentProfile.careerYears.goe(minYears) : null;
    }

    private BooleanExpression ltCareerYears(Integer maxYearsExclusive) {
        return maxYearsExclusive != null ? talentProfile.careerYears.lt(maxYearsExclusive) : null;
    }

    private BooleanExpression eqRegionId(Long regionId) {
        return regionId != null ? talentProfile.region.id.eq(regionId) : null;
    }

    // 통합 검색: 제목 / 작성자 이름 / 요구 기술스택 중 하나라도 겹치면 매칭(OR)
    private BooleanExpression matchesKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;

        BooleanExpression byTitle = portfolio.title.containsIgnoreCase(keyword);
        BooleanExpression byAuthorName = member.name.containsIgnoreCase(keyword);
        BooleanExpression byTechStack = portfolio.id.in(
                JPAExpressions.select(portfolioTechstack.portfolio.id)
                        .from(portfolioTechstack)
                        .join(portfolioTechstack.techstack, techstack)
                        .where(techstack.name.containsIgnoreCase(keyword))
        );

        return byTitle.or(byAuthorName).or(byTechStack);
    }

    private OrderSpecifier<?> getSortOrder(PortfolioSortType sortType) {
        return switch (sortType) {
            case POPULAR -> portfolio.bookmarkCount.desc();
            case MOST_VIEWED -> portfolio.viewCount.desc();
            case LATEST -> portfolio.createdAt.desc();
        };
    }
}
