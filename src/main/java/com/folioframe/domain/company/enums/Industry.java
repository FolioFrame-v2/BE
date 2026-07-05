package com.folioframe.domain.company.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Industry {
    AI_ML("AI/ML"),
    ECOMMERCE("이커머스"),
    COLLABORATION_TOOL("협업툴"),
    HEALTHCARE("헬스케어"),
    EDUTECH("에듀테크"),
    MEDIA("미디어"),
    IOT("IoT"),
    INFRASTRUCTURE("인프라");

    private final String label;
}