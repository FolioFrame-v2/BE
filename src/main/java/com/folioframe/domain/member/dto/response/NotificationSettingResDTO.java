package com.folioframe.domain.member.dto.response;

import com.folioframe.domain.member.entity.NotificationSetting;
import com.folioframe.domain.member.enums.NotificationSettingType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationSettingResDTO {
    private NotificationSettingType notificationType;
    private boolean enabled;

    public static NotificationSettingResDTO from(NotificationSetting setting) {
        return NotificationSettingResDTO.builder()
                .notificationType(setting.getNotificationType())
                .enabled(setting.isEnabled())
                .build();
    }
}
