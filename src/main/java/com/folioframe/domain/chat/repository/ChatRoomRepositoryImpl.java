package com.folioframe.domain.chat.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.folioframe.domain.chat.entity.QChatRoom.chatRoom;
import static com.folioframe.domain.chat.entity.QChatRoomParticipant.chatRoomParticipant;
import static com.folioframe.domain.company.entity.QCompanyProfile.companyProfile;
import static com.folioframe.domain.job.entity.QJobPosting.jobPosting;
import static com.folioframe.domain.portfolio.entity.QPortfolio.portfolio;
import static com.folioframe.domain.talent.entity.QTalentProfile.talentProfile;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ChatRoomListProjection> findRoomList(Long memberId, String keyword, Pageable pageable) {
        List<Tuple> tuples = queryFactory
                .select(chatRoom, chatRoomParticipant.unreadCount)
                .from(chatRoom)
                .join(chatRoomParticipant).on(joinsCurrentParticipant(memberId))
                .leftJoin(chatRoom.companyProfile, companyProfile).fetchJoin()
                .leftJoin(chatRoom.talentProfile, talentProfile).fetchJoin()
                .leftJoin(chatRoom.portfolio, portfolio)
                .leftJoin(chatRoom.jobPosting, jobPosting)
                .where(matchesKeyword(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(chatRoom.lastMessageAt.desc().nullsLast())
                .fetch();

        List<ChatRoomListProjection> content = tuples.stream()
                .map(t -> new ChatRoomListProjection(t.get(chatRoom), t.get(chatRoomParticipant.unreadCount)))
                .toList();

        JPAQuery<Long> countQuery = queryFactory
                .select(chatRoom.count())
                .from(chatRoom)
                .join(chatRoomParticipant).on(joinsCurrentParticipant(memberId))
                .leftJoin(chatRoom.companyProfile, companyProfile)
                .leftJoin(chatRoom.talentProfile, talentProfile)
                .leftJoin(chatRoom.portfolio, portfolio)
                .leftJoin(chatRoom.jobPosting, jobPosting)
                .where(matchesKeyword(keyword));

        Long total = countQuery.fetchOne();
        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    // 요청자 본인의 참가자 행(숨기지 않은 것)에만 join — 목록 대상 자체를 이 조건으로 필터링
    private BooleanExpression joinsCurrentParticipant(Long memberId) {
        return chatRoomParticipant.chatRoom.eq(chatRoom)
                .and(chatRoomParticipant.memberId.eq(memberId))
                .and(chatRoomParticipant.hiddenAt.isNull());
    }

    // 동적 쿼리: 통합 검색 — 상대 이름(기업명/인재명) / 연결된 포폴·공고 제목 / 마지막 메시지 내용 중 하나라도 겹치면 매칭(OR)
    private BooleanExpression matchesKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;

        return companyProfile.companyName.containsIgnoreCase(keyword)
                .or(talentProfile.name.containsIgnoreCase(keyword))
                .or(portfolio.title.containsIgnoreCase(keyword))
                .or(jobPosting.title.containsIgnoreCase(keyword))
                .or(chatRoom.lastMessagePreview.containsIgnoreCase(keyword));
    }
}
