package com.folioframe.domain.chat.exception;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import com.folioframe.global.apiPayload.exception.GeneralException;

public class ChatException extends GeneralException {

    public ChatException(BaseErrorCode code) {
        super(code);
    }
}
