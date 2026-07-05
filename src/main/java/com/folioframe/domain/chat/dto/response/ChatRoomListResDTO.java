package com.folioframe.domain.chat.dto.response;

import com.folioframe.domain.member.enums.MemberType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatRoomListResDTO(
        Long chatRoomId,
        String counterpartName,
        MemberType counterpartType,
        String lastMessage,
        LocalDateTime lastMessageAt,
        int unreadCount
) {}
