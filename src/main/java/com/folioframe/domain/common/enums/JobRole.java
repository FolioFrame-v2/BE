package com.folioframe.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    CLOUD_ENGINEER("클라우드 엔지니어"),
    AI_ENGINEER("AI 엔지니어"),
    ML_ENGINEER("ML 엔지니어"),
    QA_ENGINEER("QA 엔지니어"),
    SECURITY_ENGINEER("보안 엔지니어"),
    GAME_DEVELOPER("게임 개발자"),
    EMBEDDED_DEVELOPER("임베디드 개발자"),
    PM("PM"),
    PO("PO"),
    SERVICE_PLANNER("서비스 기획자"),
    UI_DESIGNER("UI 디자이너"),
    UX_DESIGNER("UX 디자이너"),
    PRODUCT_DESIGNER("프로덕트 디자이너");

    private final String label;
}
