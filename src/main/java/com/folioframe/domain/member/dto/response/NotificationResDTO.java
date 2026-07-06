package com.folioframe.domain.member.dto.response;

import com.folioframe.domain.member.entity.Notification;
import com.folioframe.domain.member.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResDTO {
    private Long notificationId;
    private NotificationType notificationType;
    private String title;
    private String content;
    private boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    public static NotificationResDTO from(Notification notification) {
        return NotificationResDTO.builder()
                .notificationId(notification.getId())
                .notificationType(notification.getNotificationType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .read(notification.isRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
