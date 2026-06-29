package com.folioframe.domain.portfolio.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiFeedbackStatus {
    PENDING("대기 중"),
    SUCCESS("완료"),
    FAILED("실패");

    private final String label;
}
