package com.folioframe.domain.job.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmploymentType {
    FULL_TIME("정규직"),
    CONTRACT("계약직"),
    INTERN("인턴"),
    FREELANCER("프리랜서"),
    PART_TIME("파트타임"),
    REMOTE("원격근무"),
    HYBRID("하이브리드");

    private final String displayName;
}
