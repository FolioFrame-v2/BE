package com.folioframe.domain.activity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityTeamSize {
    ONE("1인"),
    TWO_TO_THREE("2~3인"),
    FOUR_TO_SIX("4~6인"),
    SEVEN_PLUS("7인+");

    private final String label;
}
