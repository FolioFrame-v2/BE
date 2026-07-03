package com.folioframe.global.auth.exception.code;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "입력 형식이 올바르지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "가입되지 않은 아이디입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),
    DUPLICATE_ID(HttpStatus.CONFLICT, "DUPLICATE_ID", "이미 사용 중인 아이디입니다."),
    TERMS_NOT_FOUND(HttpStatus.NOT_FOUND, "TERMS_NOT_FOUND", "약관 정보를 찾을 수 없습니다."),
    REQUIRED_TERMS_NOT_AGREED(HttpStatus.BAD_REQUEST, "REQUIRED_TERMS_NOT_AGREED", "필수 약관에 동의해야 회원가입할 수 있습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD_FORMAT", "비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자로 입력해주세요."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "DUPLICATE_PHONE", "이미 사용 중인 휴대폰번호입니다."),
    BUSINESS_NUMBER_REQUIRED(HttpStatus.BAD_REQUEST, "BUSINESS_NUMBER_REQUIRED", "기업 회원가입 시 사업자번호는 필수입니다."),
    DUPLICATE_BUSINESS_NUMBER(HttpStatus.CONFLICT, "DUPLICATE_BUSINESS_NUMBER", "이미 등록된 사업자번호입니다."),
    EMPTY_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "EMPTY_AUTH", "인증 정보가 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}