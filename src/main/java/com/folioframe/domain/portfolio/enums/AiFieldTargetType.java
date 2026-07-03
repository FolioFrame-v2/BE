package com.folioframe.domain.portfolio.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiFieldTargetType {
    CUSTOM_FIELD("사용자 정의 필드"),
    PORTFOLIO_ONE_LINER("포트폴리오 한줄소개"),
    PORTFOLIO_DESCRIPTION("포트폴리오 상세설명"),
    PROFILE_ONE_LINER("프로필 소개"),
    PROJECT_SUMMARY("프로젝트 요약 설명");

    private final String label;
}
