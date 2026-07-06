package com.folioframe.domain.member.repository;

import com.folioframe.domain.member.entity.NotificationSetting;
import com.folioframe.domain.member.enums.NotificationSettingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    List<NotificationSetting> findAllByMemberId(Long memberId);

    Optional<NotificationSetting> findByMemberIdAndNotificationType(Long memberId, NotificationSettingType notificationType);
}