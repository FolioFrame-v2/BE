package com.folioframe.domain.chat.controller;

import com.folioframe.domain.chat.dto.request.ChatRoomCreateReqDTO;
import com.folioframe.domain.chat.dto.response.ChatMessageResDTO;
import com.folioframe.domain.chat.dto.response.ChatRoomListResDTO;
import com.folioframe.domain.chat.dto.response.ChatRoomResDTO;
import com.folioframe.domain.chat.exception.code.ChatSuccessCode;
import com.folioframe.domain.chat.service.ChatRoomService;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.auth.CurrentMemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chat", description = "프로필 간 실시간 채팅(채팅방/메시지) API")
@RestController
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(
            summary = "채팅방 시작(get-or-create)",
            description = "포트폴리오 또는 채용 공고 상세의 '문의하기'에서 호출. " +
                    "PORTFOLIO 기준은 기업 사용자만, JOB_POSTING 기준은 인재 사용자만 시작할 수 있고, " +
                    "이미 같은 상대·origin으로 만든 방이 있으면 그 방을 그대로 반환한다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ChatRoomResDTO>> getOrCreateRoom(
            @Valid @RequestBody ChatRoomCreateReqDTO request,
            @CurrentMemberId Long memberId) {

        ChatRoomResDTO response = chatRoomService.getOrCreateRoom(request, memberId);

        return ResponseEntity.ok(ApiResponse.onSuccess(ChatSuccessCode.CHAT_ROOM_READY, response));
    }

    @Operation(
            summary = "채팅방 목록(받은함) 조회",
            description = "통합 검색(keyword) — 상대 이름 / 연결된 포폴·공고 제목 / 마지막 메시지 내용 중 하나라도 겹치면 매칭. " +
                    "내가 삭제(숨김)한 방은 목록에서 제외된다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ChatRoomListResDTO>>> getRoomList(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable,
            @CurrentMemberId Long memberId) {

        Page<ChatRoomListResDTO> response = chatRoomService.getRoomList(memberId, keyword, pageable);

        return ResponseEntity.ok(ApiResponse.onSuccess(ChatSuccessCode.CHAT_ROOM_LIST_FETCHED, response));
    }

    @Operation(summary = "채팅방 상세 조회", description = "채팅 화면 상단(상대 정보, 연결된 포폴/공고)에 표시할 정보를 조회한다.")
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<ChatRoomResDTO>> getRoomDetail(
            @PathVariable Long roomId,
            @CurrentMemberId Long memberId) {

        ChatRoomResDTO response = chatRoomService.getRoomDetail(roomId, memberId);

        return ResponseEntity.ok(ApiResponse.onSuccess(ChatSuccessCode.CHAT_ROOM_DETAIL_FETCHED, response));
    }

    @Operation(summary = "채팅 메시지 목록 조회", description = "메시지 히스토리를 최신순으로 페이징 조회하고, 조회한 사용자의 안읽음 카운트를 0으로 초기화한다.")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<Page<ChatMessageResDTO>>> getMessages(
            @PathVariable Long roomId,
            @PageableDefault(size = 30) Pageable pageable,
            @CurrentMemberId Long memberId) {

        Page<ChatMessageResDTO> response = chatRoomService.getMessages(roomId, memberId, pageable);

        return ResponseEntity.ok(ApiResponse.onSuccess(ChatSuccessCode.CHAT_MESSAGE_LIST_FETCHED, response));
    }

    @Operation(summary = "채팅방 삭제(나가기)", description = "나에게만 목록에서 숨긴다. 상대가 새 메시지를 보내면 다시 노출된다.")
    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Void>> hideRoom(
            @PathVariable Long roomId,
            @CurrentMemberId Long memberId) {

        chatRoomService.hideRoom(roomId, memberId);

        return ResponseEntity.status(ChatSuccessCode.CHAT_ROOM_DELETED.getStatus())
                .body(ApiResponse.onSuccess(ChatSuccessCode.CHAT_ROOM_DELETED, null));
    }
}
