package com.folioframe.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TermsType {
    PRIVACY("개인정보 처리방침"),
    NO_MISUSE("부정이용 금지"),
    PENALTY("페널티 정책");

    private final String label;
}
