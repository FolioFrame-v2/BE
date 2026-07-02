package com.folioframe.domain.portfolio.exception.code;

import com.folioframe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PortfolioErrorCode implements BaseErrorCode {

    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "PORTFOLIO404_1", "포트폴리오를 찾을 수 없습니다."),
    PORTFOLIO_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PORTFOLIO403_1", "해당 포트폴리오에 접근할 권한이 없습니다."),

    EDUCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "EDUCATION404_1", "학력 정보를 찾을 수 없습니다."),
    EDUCATION_NOT_IN_PORTFOLIO(HttpStatus.BAD_REQUEST, "EDUCATION400_1", "해당 포트폴리오에 속한 학력 정보가 아닙니다."),

    CERTIFICATE_NOT_FOUND(HttpStatus.NOT_FOUND, "CERTIFICATE404_1", "자격증 정보를 찾을 수 없습니다."),
    CERTIFICATE_NOT_IN_PORTFOLIO(HttpStatus.BAD_REQUEST, "CERTIFICATE400_1", "해당 포트폴리오에 속한 자격증 정보가 아닙니다."),

    CAREER_NOT_FOUND(HttpStatus.NOT_FOUND, "CAREER404_1", "경력 정보를 찾을 수 없습니다."),
    CAREER_NOT_IN_PORTFOLIO(HttpStatus.BAD_REQUEST, "CAREER400_1", "해당 포트폴리오에 속한 경력 정보가 아닙니다."),

    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT404_1", "프로젝트 정보를 찾을 수 없습니다."),
    PROJECT_NOT_IN_PORTFOLIO(HttpStatus.BAD_REQUEST, "PROJECT400_1", "해당 포트폴리오에 속한 프로젝트 정보가 아닙니다."),

    TECHSTACK_NOT_FOUND(HttpStatus.NOT_FOUND, "TECHSTACK404_1", "존재하지 않는 기술스택이 포함되어 있습니다."),

    BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "BOOKMARK409_1", "이미 북마크한 포트폴리오입니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK404_1", "북마크 정보를 찾을 수 없습니다."),

    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "TEMPLATE404_1", "템플릿을 찾을 수 없습니다."),

    PORTFOLIO_FIELD_NOT_FOUND(HttpStatus.NOT_FOUND, "FIELD404_1", "포트폴리오 필드를 찾을 수 없습니다."),
    PORTFOLIO_FIELD_NOT_IN_PORTFOLIO(HttpStatus.BAD_REQUEST, "FIELD400_1", "해당 포트폴리오에 속한 필드가 아닙니다."),

    AI_FEEDBACK_LIMIT_EXCEEDED(HttpStatus.FORBIDDEN, "AIFEEDBACK403_1", "AI 첨삭 가능 횟수를 모두 사용했습니다."),
    AI_FEEDBACK_EMPTY_CONTENT(HttpStatus.BAD_REQUEST, "AIFEEDBACK400_1", "첨삭할 내용이 없습니다."),
    AI_FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "AIFEEDBACK404_1", "AI 첨삭 결과를 찾을 수 없습니다."),
    AI_SERVICE_UNAVAILABLE(HttpStatus.BAD_GATEWAY, "AIFEEDBACK502_1", "AI 첨삭 서비스 호출에 실패했습니다."),
    AI_SERVICE_QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "AIFEEDBACK429_1", "AI 서비스 사용량이 많아 잠시 후 다시 시도해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
