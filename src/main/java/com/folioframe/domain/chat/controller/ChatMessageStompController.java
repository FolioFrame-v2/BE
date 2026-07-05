package com.folioframe.domain.chat.controller;

import com.folioframe.domain.chat.dto.request.ChatMessageSendReqDTO;
import com.folioframe.domain.chat.dto.response.ChatMessageResDTO;
import com.folioframe.domain.chat.service.ChatMessageService;
import com.folioframe.global.auth.StompPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageStompController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트 SEND 목적지: /app/chat-rooms/{roomId}/messages, 브로드캐스트: /topic/chat-rooms/{roomId}
    @MessageMapping("/chat-rooms/{roomId}/messages")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageSendReqDTO request, Principal principal) {
        Long senderId = ((StompPrincipal) principal).memberId();

        ChatMessageResDTO message = chatMessageService.sendMessage(roomId, senderId, request);

        messagingTemplate.convertAndSend("/topic/chat-rooms/" + roomId, message);
    }
}
