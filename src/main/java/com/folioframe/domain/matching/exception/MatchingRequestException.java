package com.folioframe.domain.matching.exception;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import com.folioframe.global.apiPayload.exception.GeneralException;

public class MatchingRequestException extends GeneralException {

    public MatchingRequestException(BaseErrorCode code) {
        super(code);
    }
}
