package com.folioframe.domain.portfolio.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DegreeType {
    HIGH_SCHOOL("고등학교"),
    UNIVERSITY("대학교"),
    BACHELOR("학사"),
    MASTER("석사"),
    DOCTOR("박사");

    private final String label;
}
