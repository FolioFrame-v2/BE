package com.folioframe.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
    TALENT("인재"),
    COMPANY("기업");

    private final String label;
}
