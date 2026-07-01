package com.folioframe.domain.activity.exception;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import com.folioframe.global.apiPayload.exception.GeneralException;

public class ActivityException extends GeneralException {

    public ActivityException(BaseErrorCode code) {
        super(code);
    }
}
