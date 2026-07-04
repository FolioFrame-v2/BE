package com.folioframe.domain.talent.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileVisibility {
    PUBLIC("전체 공개"),
    LINK_ONLY("링크 소유자만"),
    PRIVATE("비공개");

    private final String label;
}
