package com.folioframe.domain.talent.exception;

import com.folioframe.domain.talent.exception.code.TalentProfileErrorCode;
import com.folioframe.global.apiPayload.code.BaseErrorCode;
import com.folioframe.global.apiPayload.exception.GeneralException;

public class TalentProfileException extends GeneralException {
    private TalentProfileException(BaseErrorCode code) {
        super(code);
    }
}