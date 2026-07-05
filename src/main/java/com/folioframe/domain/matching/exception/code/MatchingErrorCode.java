package com.folioframe.domain.matching.exception.code;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MatchingErrorCode implements BaseErrorCode {

    // 400 Bad Request
    INVALID_MATCHING_REQUEST(HttpStatus.BAD_REQUEST, "MATCH400_1", "매칭 제안 요청 데이터가 유효하지 않습니다."),
    INVALID_MATCHING_STATUS(HttpStatus.BAD_REQUEST, "MATCH400_2", "매칭 상태 값이 올바르지 않습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "MATCH401_1", "인증 정보가 유효하지 않습니다. 다시 로그인해 주십시오."),

    // 403 Forbidden
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "MATCH403_1", "해당 매칭에 접근할 권한이 없습니다."),
    ONLY_COMPANY_CAN_PROPOSE(HttpStatus.FORBIDDEN, "MATCH403_2", "기업 회원만 매칭 제안을 보낼 수 있습니다."),

    // 404 Not Found
    MATCHING_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH404_1", "요청한 매칭 정보를 찾을 수 없습니다."),
    JOB_POSTING_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH404_2", "해당 채용 공고를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH404_3", "해당 회원 정보를 찾을 수 없습니다."),

    // 409 Conflict
    ALREADY_PROPOSED(HttpStatus.CONFLICT, "MATCH409_1", "이미 해당 지원자에게 매칭 제안을 보냈습니다."),
    MATCHING_ALREADY_ACCEPTED(HttpStatus.CONFLICT, "MATCH409_2", "이미 수락된 매칭입니다."),
    MATCHING_ALREADY_REJECTED(HttpStatus.CONFLICT, "MATCH409_3", "이미 거절된 매칭입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}