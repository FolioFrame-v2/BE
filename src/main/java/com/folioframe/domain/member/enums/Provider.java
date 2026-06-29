package com.folioframe.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    LOCAL("이메일"),
    GOOGLE("구글");

    private final String label;
}
