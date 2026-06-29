package com.folioframe.domain.portfolio.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectDuration {
    LESS_THAN_1_MONTH("1개월 미만"),
    ONE_TO_THREE_MONTHS("1~3개월"),
    THREE_TO_SIX_MONTHS("3~6개월"),
    SIX_TO_TWELVE_MONTHS("6~12개월"),
    OVER_ONE_YEAR("1년 이상");

    private final String label;
}
