package com.folioframe.domain.chat.exception.code;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements BaseErrorCode {

    // 400 Bad Request
    INVALID_ORIGIN_FOR_MEMBER_TYPE(HttpStatus.BAD_REQUEST, "CHAT400_1", "인재는 포트폴리오 기준으로, 기업은 채용 공고 기준으로 채팅을 시작할 수 없습니다."),

    // 403 Forbidden
    CHAT_ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHAT403_1", "해당 채팅방에 접근할 권한이 없습니다."),

    // 404 Not Found
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT404_1", "채팅방을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
