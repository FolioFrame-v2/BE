package com.folioframe.global.auth.exception.code;

import com.folioframe.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthSuccessCode implements BaseSuccessCode {
    LOGIN_SUCCESS(HttpStatus.OK, "LOGIN_SUCCESS", "로그인에 성공하였습니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "SIGNUP_SUCCESS", "회원가입이 완료되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK,"LOGOUT_SUCCESS","성공적으로 로그아웃 되었습니다."),
    REFRESH_SUCCESS(HttpStatus.OK, "REFRESH_SUCCESS", "토큰 재발급에 성공하였습니다."),
    CHECK_ID_SUCCESS(HttpStatus.OK, "CHECK_ID_SUCCESS", "사용 가능한 아이디입니다."),
    TERMS_FOUND_SUCCESS(HttpStatus.OK, "TERMS_FOUND_SUCCESS", "약관 상세 조회가 완료되었습니다.")
    ;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
