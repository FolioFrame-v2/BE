package com.folioframe.domain.job.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobPostingStatus {
    ACTIVE("채용 중"),
    CLOSING_SOON("마감 임박"),
    CLOSED("마감");

    private final String label;
}
