package com.folioframe.domain.chat.entity;

import com.folioframe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_room_participant",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_chat_room_participant_room_member",
                columnNames = {"chat_room_id", "member_id"}
        )
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatRoom chatRoom;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Builder.Default
    @Column(name = "unread_count", nullable = false)
    private int unreadCount = 0;

    // null이 아니면 해당 참가자 목록에서 숨겨진 상태. 상대가 새 메시지를 보내면 다시 null로 초기화되어 재노출된다.
    @Column(name = "hidden_at")
    private LocalDateTime hiddenAt;

    public void increaseUnreadCount() {
        this.unreadCount++;
    }

    public void resetUnreadCount() {
        this.unreadCount = 0;
    }

    public void unhide() {
        this.hiddenAt = null;
    }

    public void hide() {
        this.hiddenAt = LocalDateTime.now();
    }
}
