package com.folioframe.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CareerLevel {
    NONE("없음"),
    NEWCOMER("신입"),
    ONE_TO_THREE_YEARS("1~3년"),
    FOUR_TO_SIX_YEARS("4~6년"),
    OVER_SEVEN_YEARS("7년 이상");

    private final String displayName;
}
