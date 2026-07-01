package com.folioframe.domain.activity.exception.code;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ActivityErrorCode implements BaseErrorCode {

    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "ACTIVITY404_1", "대외활동을 찾을 수 없습니다."),
    ACTIVITY_BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "ACTIVITY_BOOKMARK409_1", "이미 북마크한 대외활동입니다."),
    ACTIVITY_BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "ACTIVITY_BOOKMARK404_1", "북마크 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
