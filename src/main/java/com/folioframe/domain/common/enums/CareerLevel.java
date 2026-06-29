package com.folioframe.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CareerLevel {
    NONE("없음"),
    UNDER_ONE_YEAR("1년 미만"),
    ONE_TO_THREE_YEARS("1~3년"),
    THREE_TO_FIVE_YEARS("3~5년"),
    FIVE_TO_SEVEN_YEARS("5~7년"),
    SEVEN_TO_TEN_YEARS("7~10년"),
    OVER_TEN_YEARS("10년 이상");

    private final String displayName;
}
