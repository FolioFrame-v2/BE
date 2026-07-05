package com.folioframe.domain.chat.repository;

import com.folioframe.domain.chat.entity.ChatRoom;

// 목록 조회 시 채팅방 엔티티 + 요청자 기준 unreadCount를 함께 담기 위한 내부 조회 전용 프로젝션
public record ChatRoomListProjection(ChatRoom chatRoom, int unreadCount) {
}
