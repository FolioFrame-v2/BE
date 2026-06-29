package com.folioframe.domain.portfolio.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortfolioVisibility {
    PUBLIC("공개"),
    LINK_ONLY("링크 공개"),
    PRIVATE("비공개");

    private final String label;
}
