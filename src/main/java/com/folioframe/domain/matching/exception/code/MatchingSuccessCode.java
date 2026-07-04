package com.folioframe.domain.matching.exception.code;

import com.folioframe.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MatchingSuccessCode implements BaseSuccessCode {

    // 200 OK
    MATCHING_LIST_FETCHED(HttpStatus.OK, "MATCH200_1", "매칭 제안 목록 조회가 완료되었습니다."),
    MATCHING_DETAIL_FETCHED(HttpStatus.OK, "MATCH200_2", "매칭 상세 조회가 완료되었습니다."),
    MATCHING_PROPOSED_LIST_FETCHED(HttpStatus.OK, "MATCH200_3", "보낸 매칭 제안 목록 조회가 완료되었습니다."),
    MATCHING_RECEIVED_LIST_FETCHED(HttpStatus.OK, "MATCH200_4", "받은 매칭 제안 목록 조회가 완료되었습니다."),

    // 201 Created
    MATCHING_PROPOSED(HttpStatus.CREATED, "MATCH201_1", "매칭 제안이 성공적으로 전송되었습니다."),

    // 200 OK (상태 변경)
    MATCHING_ACCEPTED(HttpStatus.OK, "MATCH200_5", "매칭 제안이 수락되었습니다."),
    MATCHING_REJECTED(HttpStatus.OK, "MATCH200_6", "매칭 제안이 거절되었습니다."),

    // 204 No Content
    MATCHING_DELETED(HttpStatus.NO_CONTENT, "MATCH204_1", "매칭 제안이 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}