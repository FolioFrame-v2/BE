package com.folioframe.domain.portfolio.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EducationStatus {
    ENROLLED("재학중"),
    ON_LEAVE("휴학"),
    GRADUATED("졸업"),
    DROPPED_OUT("중퇴"),
    COMPLETED("수료");

    private final String label;
}
