package com.folioframe.domain.chat.dto.response;

import com.folioframe.domain.member.enums.MemberType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageResDTO(
        Long chatMessageId,
        Long senderId,
        MemberType senderType,
        String content,
        LocalDateTime sentAt
) {}
