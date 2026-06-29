package com.folioframe.domain.matching.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchingStatus {
    PROPOSED("제안 완료"),
    ACCEPTED("승인"),
    REJECTED("거절");

    private final String label;
}
