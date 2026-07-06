package com.folioframe.domain.member.service;

import com.folioframe.domain.member.dto.request.NotificationSettingReqDTO;
import com.folioframe.domain.member.dto.response.NotificationResDTO;
import com.folioframe.domain.member.dto.response.NotificationSettingResDTO;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.entity.Notification;
import com.folioframe.domain.member.entity.NotificationSetting;
import com.folioframe.domain.member.enums.NotificationSettingType;
import com.folioframe.domain.member.enums.NotificationType;
import com.folioframe.domain.member.repository.MemberRepository;
import com.folioframe.domain.member.repository.NotificationRepository;
import com.folioframe.domain.member.repository.NotificationSettingRepository;
import com.folioframe.domain.member.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final SseEmitterRepository emitterRepository;
    private final MemberRepository memberRepository;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    @Transactional
    public SseEmitter subscribe(Long memberId, String lastEventId) {
        String emitterId = memberId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));

        String eventId = memberId + "_" + System.currentTimeMillis();
        sendNotification(emitter, eventId, emitterId, "EventStream Connected. [memberId=" + memberId + "]");

        if (lastEventId != null && !lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
        }

        return emitter;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(Long receiverId, NotificationType type, String title, String content, String linkUrl) {
        NotificationSettingType settingType = NotificationSettingType.valueOf(type.name());

        boolean isEnabled = notificationSettingRepository.findByMemberIdAndNotificationType(receiverId, settingType)
                .map(NotificationSetting::isEnabled)
                .orElse(true);

        if (!isEnabled) {
            return;
        }

        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Notification notification = notificationRepository.save(
                Notification.builder()
                        .member(receiver)
                        .notificationType(type)
                        .title(title)
                        .content(content)
                        .linkUrl(linkUrl)
                        .build()
        );

        String receiverIdStr = String.valueOf(receiverId);
        String eventId = receiverIdStr + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverIdStr);

        NotificationResDTO responseDto = NotificationResDTO.from(notification);

        emitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, notification);
            sendNotification(emitter, eventId, key, responseDto);
        });
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event().id(eventId).name("NOTIFICATION").data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    public Page<NotificationResDTO> getNotifications(Long memberId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return notificationRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId, pageRequest)
                .map(NotificationResDTO::from);
    }

    @Transactional
    public NotificationResDTO readNotification(Long memberId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndMemberId(notificationId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 권한이 없는 알림입니다."));

        notification.markAsRead();
        return NotificationResDTO.from(notification);
    }

    @Transactional
    public int readAllNotifications(Long memberId) {
        return notificationRepository.updateAllReadByMemberId(memberId);
    }

    public List<NotificationSettingResDTO> getNotificationSettings(Long memberId) {
        return notificationSettingRepository.findAllByMemberId(memberId).stream()
                .map(NotificationSettingResDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationSettingResDTO updateNotificationSetting(Long memberId, NotificationSettingReqDTO reqDTO) {
        NotificationSetting setting = notificationSettingRepository
                .findByMemberIdAndNotificationType(memberId, reqDTO.getNotificationType())
                .orElseGet(() -> {
                    Member member = memberRepository.getReferenceById(memberId);
                    return notificationSettingRepository.save(
                            NotificationSetting.builder()
                                    .member(member)
                                    .notificationType(reqDTO.getNotificationType())
                                    .enabled(reqDTO.getEnabled())
                                    .build()
                    );
                });

        setting.updateEnabled(reqDTO.getEnabled());
        return NotificationSettingResDTO.from(setting);
    }
}