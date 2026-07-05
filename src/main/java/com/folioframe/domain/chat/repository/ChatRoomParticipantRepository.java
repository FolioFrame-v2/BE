package com.folioframe.domain.chat.repository;

import com.folioframe.domain.chat.entity.ChatRoom;
import com.folioframe.domain.chat.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    Optional<ChatRoomParticipant> findByChatRoomAndMemberId(ChatRoom chatRoom, Long memberId);

    List<ChatRoomParticipant> findByChatRoom(ChatRoom chatRoom);

    boolean existsByChatRoomAndMemberId(ChatRoom chatRoom, Long memberId);

    boolean existsByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);
}
