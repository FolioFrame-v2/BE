package com.folioframe.domain.common.enums;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

// 포트폴리오 목록 필터의 클릭형 카테고리(전체/Frontend/Backend/Fullstack/Mobile/Data/DevOps/Embedded).
// JobRole(19개 세부값) 중 여러 개를 하나의 카테고리로 묶어서 보여주기 위한 필터 전용 그룹핑.
// AI_ENGINEER/ML_ENGINEER/QA/SECURITY/GAME/PM/PO는 이 7개 카테고리 어디에도 속하지 않아
// "전체"를 선택했을 때만 노출되고, 특정 카테고리를 클릭하면 걸러진다.
@Getter
public enum PortfolioJobCategory {
    FRONTEND("Frontend", EnumSet.of(JobRole.FRONTEND)),
    BACKEND("Backend", EnumSet.of(JobRole.BACKEND)),
    FULLSTACK("Fullstack", EnumSet.of(JobRole.FULLSTACK)),
    MOBILE("Mobile", EnumSet.of(JobRole.MOBILE, JobRole.ANDROID, JobRole.IOS)),
    DATA("Data", EnumSet.of(JobRole.DATA_ENGINEER, JobRole.DATA_ANALYST, JobRole.DATA_SCIENTIST)),
    DEVOPS("DevOps", EnumSet.of(JobRole.DEVOPS, JobRole.CLOUD)),
    EMBEDDED("Embedded", EnumSet.of(JobRole.EMBEDDED));

    private final String label;
    private final Set<JobRole> jobRoles;

    PortfolioJobCategory(String label, Set<JobRole> jobRoles) {
        this.label = label;
        this.jobRoles = jobRoles;
    }
}
