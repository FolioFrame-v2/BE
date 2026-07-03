package com.folioframe.domain.company.exception.code;

import com.folioframe.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CompanySuccessCode implements BaseSuccessCode {

    COMPANY_VERIFICATION_UPDATED(HttpStatus.OK, "COMPANY_VERIFICATION200_1", "기업 인증 상태가 변경되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
