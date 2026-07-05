package com.folioframe.domain.company.exception.code;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CompanyErrorCode implements BaseErrorCode {

    // 400 Bad Request
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMPANY400_1", "입력 형식이 올바르지 않거나 유효하지 않은 값이 포함되어 있습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMPANY401_1", "인증 정보가 유효하지 않습니다. 다시 로그인해 주십시오."),

    // 403 Forbidden
    FORBIDDEN_UPDATE(HttpStatus.FORBIDDEN, "COMPANY403_1", "해당 기업 프로필을 수정할 권한이 없습니다."),

    // 404 Not Found
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY404_1", "등록된 기업 프로필 정보를 찾을 수 없습니다."),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY404_2", "요청하신 기업 프로필 정보를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY404_3", "사용자를 찾을 수 없습니다."),
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY404_4", "유효하지 않은 지역입니다."),
    COMPANY_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY_PROFILE404_1", "기업 프로필 정보를 찾을 수 없습니다."),

    // 409 Conflict
    DUPLICATE_COMPANY_PROFILE(HttpStatus.CONFLICT, "COMPANY409_1", "해당 계정에 연결된 기업 프로필이 이미 존재합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}