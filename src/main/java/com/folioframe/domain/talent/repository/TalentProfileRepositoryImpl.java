package com.folioframe.domain.talent.repository;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.JobRole;
import com.folioframe.domain.job.enums.EmploymentType;
import com.folioframe.domain.talent.dto.response.TalentProfileSimpleResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.folioframe.domain.talent.entity.QTalentProfile.talentProfile;
import static com.folioframe.domain.talent.entity.QTalentTechstack.talentTechstack;
import static com.folioframe.domain.common.entity.QTechstack.techstack;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class TalentProfileRepositoryImpl implements TalentProfileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TalentProfileSimpleResponse> searchDynamic(
            String sort, CareerLevel career, EmploymentType employment, String techStack, JobRole job, Pageable pageable) {

        List<Long> profileIds = queryFactory
                .select(talentProfile.id)
                .from(talentProfile)
                .where(
                        careerEq(career),
                        jobEq(job),
                        employmentEq(employment),
                        techStackEq(techStack)
                )
                .orderBy(getSortOrder(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (profileIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Long total = queryFactory
                .select(talentProfile.count())
                .from(talentProfile)
                .where(
                        careerEq(career),
                        jobEq(job),
                        employmentEq(employment),
                        techStackEq(techStack)
                )
                .fetchOne();

        List<TalentProfileSimpleResponse> content = queryFactory
                .from(talentProfile)
                .leftJoin(talentTechstack).on(talentTechstack.talentProfile.eq(talentProfile))
                .leftJoin(talentTechstack.techstack, techstack)
                .where(talentProfile.id.in(profileIds))
                .orderBy(getSortOrder(sort))
                .transform(
                        groupBy(talentProfile.id).list(
                                Projections.constructor(TalentProfileSimpleResponse.class,
                                        talentProfile.id,
                                        talentProfile.name,
                                        talentProfile.oneLiner,
                                        talentProfile.jobRole.stringValue(),
                                        talentProfile.careerLevel.stringValue(),
                                        list(techstack.name),
                                        talentProfile.viewCount,
                                        talentProfile.bookmarkCount
                                )
                        )
                );

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression careerEq(CareerLevel career) {
        return career != null ? talentProfile.careerLevel.eq(career) : null;
    }

    private BooleanExpression jobEq(JobRole job) {
        return job != null ? talentProfile.jobRole.eq(job) : null;
    }

    private BooleanExpression employmentEq(EmploymentType employment) {
        return employment != null ? talentProfile.employmentType.eq(employment) : null;
    }

    private BooleanExpression techStackEq(String techStack) {
        if (!StringUtils.hasText(techStack)) return null;

        return talentProfile.id.in(
                JPAExpressions.select(talentTechstack.talentProfile.id)
                        .from(talentTechstack)
                        .join(talentTechstack.techstack, techstack)
                        .where(techstack.name.eq(techStack))
        );
    }

    private OrderSpecifier<?> getSortOrder(String sort) {
        if (!StringUtils.hasText(sort)) {
            return talentProfile.createdAt.desc();
        }

        return switch (sort.toUpperCase()) {
            case "VIEW" -> talentProfile.viewCount.desc();
            case "POPULAR" -> talentProfile.bookmarkCount.desc();
            default -> talentProfile.createdAt.desc();
        };
    }
}