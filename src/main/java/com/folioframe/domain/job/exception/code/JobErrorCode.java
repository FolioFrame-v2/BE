package com.folioframe.domain.job.exception.code;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JobErrorCode implements BaseErrorCode {

    // --- Posting 관련 에러 ---
    INVALID_QUERY_PARAM(HttpStatus.BAD_REQUEST, "JOB400_1", "페이지 번호나 정렬 방식 등 요청 파라미터가 유효하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "JOB401_1", "인증 정보가 유효하지 않습니다. 다시 로그인해 주십시오."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "JOB403_1", "해당 채용 공고를 제어할 권한이 없습니다."),
    JOB_POSTING_NOT_FOUND(HttpStatus.NOT_FOUND, "JOB404_1", "요청하신 채용 공고 정보를 찾을 수 없습니다."),
    JOB_POSTING_CANNOT_DELETE(HttpStatus.CONFLICT, "JOB409_1", "지원자가 존재하여 공고를 삭제할 수 없습니다. 공고를 마감 처리해 주십시오."),

    // --- Application 관련 에러 ---
    APP_BAD_REQUEST(HttpStatus.BAD_REQUEST, "APP400_1", "요청 형식이 올바르지 않거나 필수 정보가 누락되었습니다."),
    INVALID_STATUS_VALUE(HttpStatus.BAD_REQUEST, "APP400_2", "유효하지 않은 상태 값입니다."),
    APP_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "APP401_1", "인증 정보가 유효하지 않습니다. 다시 로그인해 주십시오."),
    APP_FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "APP403_1", "해당 지원 내역의 상태를 변경할 권한이 없습니다."),
    APP_RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "APP404_1", "지원하려는 채용 공고 또는 포트폴리오를 찾을 수 없습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APP404_2", "요청하신 지원 내역을 찾을 수 없습니다."),
    DUPLICATE_APPLICATION(HttpStatus.CONFLICT, "APP409_1", "이미 해당 채용 공고에 지원하셨습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}