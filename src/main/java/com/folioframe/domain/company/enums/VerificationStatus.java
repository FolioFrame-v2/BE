package com.folioframe.domain.company.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerificationStatus {
    PENDING("검토 중"),
    VERIFIED("인증 완료"),
    REJECTED("반려");

    private final String label;
}
