package com.folioframe.global.auth;

import com.folioframe.domain.chat.repository.ChatRoomParticipantRepository;
import com.folioframe.domain.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// STOMP CONNECT 프레임의 Authorization 헤더로 기존 JWT 인증을 재사용하고(브라우저 네이티브 WebSocket은
// 커스텀 HTTP 헤더를 못 보내므로 프레임 레벨에서 처리), SUBSCRIBE 시점엔 요청자가 그 채팅방의
// 참가자인지 검증해 권한 없는 방 구독을 막는다.
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final Pattern CHAT_ROOM_TOPIC_PATTERN = Pattern.compile("^/topic/chat-rooms/(\\d+)$");

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenService tokenService;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticate(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            authorizeSubscription(accessor);
        }

        return message;
    }

    private void authenticate(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new MessagingException("인증 정보가 없습니다.");
        }

        String token = authorization.substring(7);
        if (tokenService.isBlacklisted(token)) {
            throw new MessagingException("유효하지 않은 토큰입니다.");
        }
        jwtUtil.validateToken(token);

        String loginId = jwtUtil.getUserId(token);
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);

        accessor.setUser(new StompPrincipal(userDetails.member().getId()));
    }

    private void authorizeSubscription(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null) {
            return;
        }

        Matcher matcher = CHAT_ROOM_TOPIC_PATTERN.matcher(destination);
        if (!matcher.matches()) {
            return;
        }

        Long roomId = Long.valueOf(matcher.group(1));
        Long memberId = resolveMemberId(accessor);

        if (!chatRoomParticipantRepository.existsByChatRoomIdAndMemberId(roomId, memberId)) {
            throw new MessagingException("해당 채팅방을 구독할 권한이 없습니다.");
        }
    }

    private Long resolveMemberId(StompHeaderAccessor accessor) {
        if (accessor.getUser() instanceof StompPrincipal principal) {
            return principal.memberId();
        }
        throw new MessagingException("인증 정보가 없습니다.");
    }
}
