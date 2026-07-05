package com.folioframe.domain.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomRepositoryCustom {

    // 통합 검색(keyword): 상대 이름 / 연결된 포폴·공고 제목 / 마지막 메시지 내용 중 하나라도 겹치면 매칭(OR)
    // 요청자(memberId) 기준으로 숨겨지지 않은(hiddenAt IS NULL) 채팅방만 대상
    Page<ChatRoomListProjection> findRoomList(Long memberId, String keyword, Pageable pageable);
}
