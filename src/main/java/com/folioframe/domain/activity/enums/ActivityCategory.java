package com.folioframe.domain.activity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityCategory {
    CONTEST("공모전"),
    HACKATHON("해커톤");

    private final String label;
}
