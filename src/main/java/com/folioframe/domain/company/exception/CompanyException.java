package com.folioframe.domain.company.exception;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import com.folioframe.global.apiPayload.exception.GeneralException;

public class CompanyException extends GeneralException {

    public CompanyException(BaseErrorCode code) {
        super(code);
    }
}
