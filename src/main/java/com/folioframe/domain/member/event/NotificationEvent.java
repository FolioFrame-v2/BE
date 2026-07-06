package com.folioframe.domain.member.event;

import com.folioframe.domain.member.enums.NotificationType;

public record NotificationEvent(
        Long receiverId,
        NotificationType type,
        String title,
        String content
) {
}