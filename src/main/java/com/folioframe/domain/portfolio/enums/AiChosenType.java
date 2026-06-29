package com.folioframe.domain.portfolio.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiChosenType {
    ORIGINAL("원본"),
    AI("AI 수정본"),
    PENDING("미선택");

    private final String label;
}
