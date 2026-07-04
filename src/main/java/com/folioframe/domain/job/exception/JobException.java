package com.folioframe.domain.job.exception;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import com.folioframe.global.apiPayload.exception.GeneralException;

public class JobException extends GeneralException {

    public JobException(BaseErrorCode code) {
        super(code);
    }
}
