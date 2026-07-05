package com.folioframe.domain.chat.service;

import com.folioframe.domain.chat.dto.request.ChatMessageSendReqDTO;
import com.folioframe.domain.chat.dto.response.ChatMessageResDTO;
import com.folioframe.domain.chat.entity.ChatMessage;
import com.folioframe.domain.chat.entity.ChatRoom;
import com.folioframe.domain.chat.entity.ChatRoomParticipant;
import com.folioframe.domain.chat.exception.ChatException;
import com.folioframe.domain.chat.exception.code.ChatErrorCode;
import com.folioframe.domain.chat.repository.ChatMessageRepository;
import com.folioframe.domain.chat.repository.ChatRoomParticipantRepository;
import com.folioframe.domain.chat.repository.ChatRoomRepository;
import com.folioframe.domain.member.enums.MemberType;
import com.folioframe.global.apiPayload.code.GeneralErrorCode;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private static final int PREVIEW_MAX_LENGTH = 500;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatMessageResDTO sendMessage(Long roomId, Long senderId, ChatMessageSendReqDTO request) {
        if (!StringUtils.hasText(request.content())) {
            throw new GeneralException(GeneralErrorCode.VALID_FAIL);
        }

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        List<ChatRoomParticipant> participants = participantRepository.findByChatRoom(room);
        boolean senderIsParticipant = participants.stream().anyMatch(p -> p.getMemberId().equals(senderId));
        if (!senderIsParticipant) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }

        ChatMessage message = chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(room)
                .senderId(senderId)
                .content(request.content())
                .build());

        LocalDateTime sentAt = message.getCreatedAt();
        room.updateLastMessage(truncate(request.content()), sentAt);

        for (ChatRoomParticipant participant : participants) {
            participant.unhide();
            if (!participant.getMemberId().equals(senderId)) {
                participant.increaseUnreadCount();
            }
        }

        MemberType senderType = room.getCompanyProfile().getMember().getId().equals(senderId)
                ? MemberType.COMPANY
                : MemberType.TALENT;

        return ChatMessageResDTO.builder()
                .chatMessageId(message.getId())
                .senderId(senderId)
                .senderType(senderType)
                .content(message.getContent())
                .sentAt(sentAt)
                .build();
    }

    private String truncate(String content) {
        if (content == null || content.length() <= PREVIEW_MAX_LENGTH) return content;
        return content.substring(0, PREVIEW_MAX_LENGTH);
    }
}
