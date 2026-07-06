package com.folioframe.domain.member.controller;

import com.folioframe.domain.member.dto.request.NotificationSettingReqDTO;
import com.folioframe.domain.member.service.NotificationService;
import com.folioframe.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import jakarta.validation.Valid;

@Tag(name = "Notification API", description = "알림 및 SSE 연결 관련 API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "실시간 알림 구독 (SSE)", description = "클라이언트가 서버와 실시간 알림을 받기 위해 SSE 연결을 맺습니다.")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return ResponseEntity.ok(notificationService.subscribe(userDetails.member().getId(), lastEventId));
    }

    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 수신함 목록을 페이징하여 조회합니다. (최신순 기본 정렬)")
    @GetMapping
    public ResponseEntity<?> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "9")
            @RequestParam(defaultValue = "9") int size) {

        int pageNumber = Math.max(0, page - 1);

        return ResponseEntity.ok(notificationService.getNotifications(
                userDetails.member().getId(), pageNumber, size));
    }

    @Operation(summary = "단일 알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<?> readNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.readNotification(userDetails.member().getId(), notificationId));
    }

    @Operation(summary = "전체 알림 읽음 처리", description = "사용자의 모든 미읽음 알림을 일괄 읽음 처리합니다.")
    @PatchMapping("/read-all")
    public ResponseEntity<?> readAllNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.readAllNotifications(userDetails.member().getId());
        return ResponseEntity.ok().body("모든 알림 읽음 처리 완료");
    }

    @Operation(summary = "알림 설정 조회", description = "사용자의 알림 수신 여부(On/Off) 설정 목록을 조회합니다.")
    @GetMapping("/settings")
    public ResponseEntity<?> getNotificationSettings(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getNotificationSettings(userDetails.member().getId()));
    }

    @Operation(summary = "알림 설정 변경", description = "특정 유형의 알림 수신 여부(On/Off)를 변경합니다.")
    @PatchMapping("/settings")
    public ResponseEntity<?> updateNotificationSetting(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody NotificationSettingReqDTO reqDTO) {
        return ResponseEntity.ok(notificationService.updateNotificationSetting(userDetails.member().getId(), reqDTO));
    }
}