package com.folioframe.domain.portfolio.exception;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import com.folioframe.global.apiPayload.exception.GeneralException;

public class PortfolioException extends GeneralException {

    public PortfolioException(BaseErrorCode code) {
        super(code);
    }
}
