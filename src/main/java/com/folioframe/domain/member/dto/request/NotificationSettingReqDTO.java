package com.folioframe.domain.member.dto.request;

import com.folioframe.domain.member.enums.NotificationSettingType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationSettingReqDTO {

    @NotNull(message = "알림 설정 유형은 필수입니다.")
    private NotificationSettingType notificationType;

    @NotNull(message = "활성화 여부는 필수입니다.")
    private Boolean enabled;
}
