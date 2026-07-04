package com.folioframe.domain.job.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobSeekingStatus {
    SEEKING("구직 중"),
    CONSIDERING("이직 고려 중"),
    EMPLOYED("재직 중");

    private final String label;
}
