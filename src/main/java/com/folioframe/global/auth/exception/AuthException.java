package com.folioframe.global.auth.exception;

import com.folioframe.global.apiPayload.exception.GeneralException;
import com.folioframe.global.auth.exception.code.AuthErrorCode;

public class AuthException extends GeneralException {

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}