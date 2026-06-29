package com.folioframe.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    COMPANY_PROPOSAL("기업 매칭 제안"),
    JOB_APPLICATION("채용 지원"),
    APPLICATION_RESULT("지원 결과"),
    INTEREST_MATCH("관심 매칭");

    private final String label;
}
