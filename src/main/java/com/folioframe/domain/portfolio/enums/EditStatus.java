package com.folioframe.domain.portfolio.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EditStatus {
    DRAFT("초안"),
    IN_PROGRESS("작성 중"),
    REVIEW("검토 중"),
    PUBLISHED("게시됨");

    private final String label;
}
