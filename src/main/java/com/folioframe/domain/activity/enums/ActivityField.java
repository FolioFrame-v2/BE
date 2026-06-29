package com.folioframe.domain.activity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityField {
    AI_ML("AI/ML"),
    HEALTHCARE("헬스케어"),
    EDUTECH("에듀테크"),
    LIFESTYLE("라이프스타일"),
    OPEN_SOURCE("오픈소스"),
    INFRA("인프라");

    private final String label;
}
