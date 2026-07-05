package com.folioframe.domain.job.exception.code;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JobErrorCode implements BaseErrorCode {

    // 400 Bad Request
    INVALID_QUERY_PARAM(HttpStatus.BAD_REQUEST, "JOB400_1", "페이지 번호나 정렬 방식 등 요청 파라미터가 유효하지 않습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "JOB401_1", "인증 정보가 유효하지 않습니다. 다시 로그인해 주십시오."),

    // 403 Forbidden
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "JOB403_1", "해당 채용 공고를 제어할 권한이 없습니다."),

    // 404 Not Found
    JOB_POSTING_NOT_FOUND(HttpStatus.NOT_FOUND, "JOB404_1", "요청하신 채용 공고 정보를 찾을 수 없습니다."),

    // 409 Conflict
    JOB_POSTING_CANNOT_DELETE(HttpStatus.CONFLICT, "JOB409_1", "지원자가 존재하여 공고를 삭제할 수 없습니다. 공고를 마감 처리해 주십시오.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}