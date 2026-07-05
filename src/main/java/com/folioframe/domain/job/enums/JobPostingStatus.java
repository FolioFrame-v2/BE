package com.folioframe.domain.job.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public enum JobPostingStatus {
    ACTIVE("채용 중"),
    CLOSING_SOON("마감 임박"),
    CLOSED("마감");

    private static final int CLOSING_SOON_THRESHOLD_DAYS = 7;

    private final String label;

    // 화면에 보여줄 실제 상태: 마감일 기준으로 파생 계산한다(CLOSED로 수동 지정한 경우는 그대로 존중).
    // - 등록자가 CLOSED로 직접 지정했으면 그대로 CLOSED
    // - deadline이 지났으면 CLOSED
    // - deadline이 오늘부터 7일 이내면 CLOSING_SOON
    // - 그 외엔 ACTIVE
    public static JobPostingStatus resolve(JobPostingStatus stored, LocalDate deadline) {
        if (stored == CLOSED) return CLOSED;
        if (deadline == null) return stored;

        LocalDate today = LocalDate.now();
        if (deadline.isBefore(today)) return CLOSED;
        if (!deadline.isAfter(today.plusDays(CLOSING_SOON_THRESHOLD_DAYS))) return CLOSING_SOON;
        return ACTIVE;
    }
}
