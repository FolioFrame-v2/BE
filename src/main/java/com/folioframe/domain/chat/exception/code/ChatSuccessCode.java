package com.folioframe.domain.chat.exception.code;

import com.folioframe.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatSuccessCode implements BaseSuccessCode {

    // 200 OK
    CHAT_ROOM_READY(HttpStatus.OK, "CHAT200_1", "채팅방이 준비되었습니다."),
    CHAT_ROOM_LIST_FETCHED(HttpStatus.OK, "CHAT200_2", "채팅방 목록 조회가 완료되었습니다."),
    CHAT_ROOM_DETAIL_FETCHED(HttpStatus.OK, "CHAT200_3", "채팅방 상세 조회가 완료되었습니다."),
    CHAT_MESSAGE_LIST_FETCHED(HttpStatus.OK, "CHAT200_4", "채팅 메시지 목록 조회가 완료되었습니다."),

    // 204 No Content
    CHAT_ROOM_DELETED(HttpStatus.NO_CONTENT, "CHAT204_1", "채팅방이 목록에서 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
