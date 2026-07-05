package com.folioframe.domain.talent.exception.code;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TalentProfileErrorCode implements BaseErrorCode {

    // 400 Bad Request
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "TALENT400_1", "입력 형식이 올바르지 않거나 필수 값이 누락되었습니다."),
    INVALID_FILTER_OPTION(HttpStatus.BAD_REQUEST, "TALENT400_2", "지원하지 않는 필터 조건입니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "TALENT401_1", "인증 정보가 유효하지 않습니다. 다시 로그인해 주십시오."),

    // 404 Not Found
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "TALENT404_1", "등록된 프로필을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "TALENT404_2", "사용자를 찾을 수 없습니다."),
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "TALENT404_3", "유효하지 않은 지역입니다."),
    CAREER_NOT_FOUND(HttpStatus.NOT_FOUND, "TALENT404_4", "경력 정보를 찾을 수 없습니다."),
    EDUCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "TALENT404_5", "학력 정보를 찾을 수 없습니다."),
    CERTIFICATE_NOT_FOUND(HttpStatus.NOT_FOUND, "TALENT404_6", "자격증 정보를 찾을 수 없습니다."),

    // 400 Bad Request (소유권)
    CAREER_NOT_IN_PROFILE(HttpStatus.BAD_REQUEST, "TALENT400_3", "본인 프로필에 속한 경력 정보가 아닙니다."),
    EDUCATION_NOT_IN_PROFILE(HttpStatus.BAD_REQUEST, "TALENT400_4", "본인 프로필에 속한 학력 정보가 아닙니다."),
    CERTIFICATE_NOT_IN_PROFILE(HttpStatus.BAD_REQUEST, "TALENT400_5", "본인 프로필에 속한 자격증 정보가 아닙니다."),

    // 409 Conflict
    PROFILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "TALENT409_1", "이미 등록된 인재 프로필이 존재합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}