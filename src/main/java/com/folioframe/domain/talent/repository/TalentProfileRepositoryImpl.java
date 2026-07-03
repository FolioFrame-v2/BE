package com.folioframe.domain.talent.repository;

import com.folioframe.domain.common.enums.CareerLevel;
import com.folioframe.domain.common.enums.JobRole;
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
import static com.folioframe.domain.member.entity.QMember.member;
import static com.folioframe.domain.talent.entity.QTalentTechstack.talentTechstack;
import static com.folioframe.domain.common.entity.QTechstack.techstack;
import static com.folioframe.domain.talent.entity.QTalentJobRole.talentJobRole;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class TalentProfileRepositoryImpl implements TalentProfileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TalentProfileSimpleResponse> searchDynamic(
            // String에서 CareerLevel, JobRole 타입으로 변경
            String sort, CareerLevel career, String employment, String techStack, JobRole job, Pageable pageable) {

        List<Long> profileIds = queryFactory
                .select(talentProfile.id)
                .from(talentProfile)
                .where(
                        careerEq(career),
                        jobEq(job)
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
                        jobEq(job)
                )
                .fetchOne();

        List<TalentProfileSimpleResponse> content = queryFactory
                .from(talentProfile)
                .leftJoin(talentProfile.member, member)
                .leftJoin(talentTechstack).on(talentTechstack.talentProfile.eq(talentProfile))
                .leftJoin(talentTechstack.techstack, techstack)
                .where(talentProfile.id.in(profileIds))
                .orderBy(getSortOrder(sort))
                .transform(
                        groupBy(talentProfile.id).list(
                                Projections.constructor(TalentProfileSimpleResponse.class,
                                        talentProfile.id,
                                        member.name,
                                        talentProfile.jobTitle,
                                        talentProfile.careerLevel.stringValue(),
                                        list(techstack.name),
                                        talentProfile.viewCount,
                                        talentProfile.bookmarkCount
                                )
                        )
                );

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    // =========================================================================
    // 파라미터가 Enum으로 바뀌면서 매핑 로직이 초간단하게 변함
    // =========================================================================

    private BooleanExpression careerEq(CareerLevel career) {
        // null이 아니면 바로 career_level 일치 조건 반환
        return career != null ? talentProfile.careerLevel.eq(career) : null;
    }

    private BooleanExpression jobEq(JobRole job) {
        // null이 아니면 서브쿼리를 통한 job_role 일치 조건 반환
        return job != null ? JPAExpressions.selectOne()
                .from(talentJobRole)
                .where(talentJobRole.talentProfile.eq(talentProfile)
                        .and(talentJobRole.jobRole.eq(job)))
                .exists() : null;
    }

    private OrderSpecifier<?> getSortOrder(String sort) {
        if (!StringUtils.hasText(sort)) {
            return talentProfile.createdAt.desc(); // 기본 정렬: 최신순
        }

        return switch (sort.toUpperCase()) {
            case "VIEW" -> talentProfile.viewCount.desc();
            case "POPULAR" -> talentProfile.bookmarkCount.desc();
            default -> talentProfile.createdAt.desc();
        };
    }
}