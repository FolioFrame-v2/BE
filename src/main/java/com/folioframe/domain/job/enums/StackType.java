package com.folioframe.domain.job.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StackType {
    REQUIRED("필수"),
    PREFERRED("우대");

    private final String label;
}
