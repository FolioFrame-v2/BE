package com.folioframe.domain.job.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationStatus {
    APPLIED("지원 완료"),
    ACCEPTED("승인"),
    REJECTED("거절");

    private final String label;
}
