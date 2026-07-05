package com.folioframe.domain.chat.dto.response;

import com.folioframe.domain.chat.enums.ChatOriginType;
import com.folioframe.domain.member.enums.MemberType;
import lombok.Builder;

@Builder
public record ChatRoomResDTO(
        Long chatRoomId,
        String counterpartName,
        MemberType counterpartType,
        ChatOriginType originType,
        Long originId,
        String originTitle
) {}
