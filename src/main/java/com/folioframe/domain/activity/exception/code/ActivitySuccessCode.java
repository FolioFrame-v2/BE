package com.folioframe.domain.activity.exception.code;

import com.folioframe.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ActivitySuccessCode implements BaseSuccessCode {

    ACTIVITY_LIST_FOUND(HttpStatus.OK, "ACTIVITY200_1", "대외활동 목록을 조회했습니다."),
    ACTIVITY_VIEW_COUNTED(HttpStatus.OK, "ACTIVITY200_2", "조회수가 반영되었습니다."),
    ACTIVITY_BOOKMARK_CREATED(HttpStatus.CREATED, "ACTIVITY_BOOKMARK201_1", "대외활동을 북마크했습니다."),
    ACTIVITY_BOOKMARK_DELETED(HttpStatus.OK, "ACTIVITY_BOOKMARK200_1", "대외활동 북마크를 취소했습니다."),
    ACTIVITY_BOOKMARKED_LIST_FOUND(HttpStatus.OK, "ACTIVITY_BOOKMARK200_2", "북마크한 대외활동 목록을 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
