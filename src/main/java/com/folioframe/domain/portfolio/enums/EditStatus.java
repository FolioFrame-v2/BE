package com.folioframe.domain.portfolio.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EditStatus {
    DRAFT("초안"),
    PUBLISHED("게시됨");

    private final String label;
}
