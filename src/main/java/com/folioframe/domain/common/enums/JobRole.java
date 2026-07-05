package com.folioframe.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum JobRole {
    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    FULLSTACK("풀스택"),
    MOBILE("모바일"),
    ANDROID("안드로이드"),
    IOS("iOS"),
    DATA_ENGINEER("데이터 엔지니어"),
    DATA_ANALYST("데이터 분석가"),
    DATA_SCIENTIST("데이터 사이언티스트"),
    DEVOPS("DevOps"),
    CLOUD("클라우드"),
    AI_ENGINEER("AI 엔지니어"),
    ML_ENGINEER("ML 엔지니어"),
    QA("QA"),
    SECURITY("보안"),
    GAME("게임"),
    EMBEDDED("임베디드"),
    PM("PM"),
    PO("PO");

    private final String label;

    // "직무" 자유 검색용 — 한글 라벨/영문 이름 중 하나라도 겹치면 매칭 (예: "프론트" → FRONTEND, "back" → BACKEND)
    public static List<JobRole> findByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();

        String lower = keyword.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(role -> role.label.toLowerCase().contains(lower) || role.name().toLowerCase().contains(lower))
                .toList();
    }
}
