package com.folioframe.domain.company.exception.code;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CompanyErrorCode implements BaseErrorCode {

    COMPANY_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY_PROFILE404_1", "기업 프로필 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
