package com.folioframe.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CareerLevel {
    NONE("없음", 0, 0),
    UNDER_ONE_YEAR("1년 미만", 0, 1),
    ONE_TO_THREE_YEARS("1~3년", 1, 3),
    THREE_TO_FIVE_YEARS("3~5년", 3, 5),
    FIVE_TO_SEVEN_YEARS("5~7년", 5, 7),
    SEVEN_TO_TEN_YEARS("7~10년", 7, 10),
    OVER_TEN_YEARS("10년 이상", 10, Integer.MAX_VALUE);

    private final String displayName;
    // 연차 → 버킷 변환 시 사용하는 최소/최대(미포함) 연차 경계값
    private final int minYears;
    private final int maxYearsExclusive;

    public static CareerLevel fromYears(Integer years) {
        if (years == null) return null;
        for (CareerLevel level : values()) {
            if (level == NONE) continue;
            if (years >= level.minYears && years < level.maxYearsExclusive) return level;
        }
        return years <= 0 ? NONE : OVER_TEN_YEARS;
    }
}
