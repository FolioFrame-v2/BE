package com.folioframe.domain.chat.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatOriginType {
    PORTFOLIO("포트폴리오"),
    JOB_POSTING("채용 공고");

    private final String label;
}
