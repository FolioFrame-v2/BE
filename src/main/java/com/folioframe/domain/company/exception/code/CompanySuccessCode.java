package com.folioframe.domain.company.exception.code;

import com.folioframe.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CompanySuccessCode implements BaseSuccessCode {

    // 200 OK
    SIGNUP_INFO_FETCHED(HttpStatus.OK, "COMPANY200_5", "회원가입 정보 조회가 완료되었습니다."),
    PROFILE_FETCHED(HttpStatus.OK, "COMPANY200_1", "내 기업 프로필 조회가 완료되었습니다."),
    COMPANY_FETCHED(HttpStatus.OK, "COMPANY200_2", "기업 프로필 상세 조회가 완료되었습니다."),
    PROFILE_UPDATED(HttpStatus.OK, "COMPANY200_3", "기업 프로필 수정이 완료되었습니다."),
    COMPANY_VERIFICATION_UPDATED(HttpStatus.OK, "COMPANY200_4", "기업 프로필 인증 상태가 변경되었습니다."),

    // 201 Created
    PROFILE_CREATED(HttpStatus.CREATED, "COMPANY201_1", "기업 프로필 등록이 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}