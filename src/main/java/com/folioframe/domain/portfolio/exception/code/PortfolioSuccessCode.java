package com.folioframe.domain.portfolio.exception.code;

import com.folioframe.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PortfolioSuccessCode implements BaseSuccessCode {

    PORTFOLIO_CREATED(HttpStatus.CREATED, "PORTFOLIO201_1", "포트폴리오가 생성되었습니다."),
    PORTFOLIO_LIST_FOUND(HttpStatus.OK, "PORTFOLIO200_1", "포트폴리오 목록을 조회했습니다."),
    PORTFOLIO_DETAIL_FOUND(HttpStatus.OK, "PORTFOLIO200_2", "포트폴리오 상세를 조회했습니다."),
    PORTFOLIO_UPDATED(HttpStatus.OK, "PORTFOLIO200_3", "포트폴리오가 수정되었습니다."),
    PORTFOLIO_VISIBILITY_CHANGED(HttpStatus.OK, "PORTFOLIO200_4", "포트폴리오 공개 설정이 변경되었습니다."),
    PORTFOLIO_PUBLISHED(HttpStatus.OK, "PORTFOLIO200_5", "포트폴리오가 게시되었습니다."),
    PORTFOLIO_DELETED(HttpStatus.OK, "PORTFOLIO200_6", "포트폴리오가 삭제되었습니다."),

    EDUCATION_CREATED(HttpStatus.CREATED, "EDUCATION201_1", "학력 정보가 등록되었습니다."),
    EDUCATION_LIST_FOUND(HttpStatus.OK, "EDUCATION200_1", "학력 목록을 조회했습니다."),
    EDUCATION_UPDATED(HttpStatus.OK, "EDUCATION200_2", "학력 정보가 수정되었습니다."),
    EDUCATION_DELETED(HttpStatus.OK, "EDUCATION200_3", "학력 정보가 삭제되었습니다."),

    CERTIFICATE_CREATED(HttpStatus.CREATED, "CERTIFICATE201_1", "자격증 정보가 등록되었습니다."),
    CERTIFICATE_LIST_FOUND(HttpStatus.OK, "CERTIFICATE200_1", "자격증 목록을 조회했습니다."),
    CERTIFICATE_UPDATED(HttpStatus.OK, "CERTIFICATE200_2", "자격증 정보가 수정되었습니다."),
    CERTIFICATE_DELETED(HttpStatus.OK, "CERTIFICATE200_3", "자격증 정보가 삭제되었습니다."),

    CAREER_CREATED(HttpStatus.CREATED, "CAREER201_1", "경력 정보가 등록되었습니다."),
    CAREER_LIST_FOUND(HttpStatus.OK, "CAREER200_1", "경력 목록을 조회했습니다."),
    CAREER_UPDATED(HttpStatus.OK, "CAREER200_2", "경력 정보가 수정되었습니다."),
    CAREER_DELETED(HttpStatus.OK, "CAREER200_3", "경력 정보가 삭제되었습니다."),

    PROJECT_CREATED(HttpStatus.CREATED, "PROJECT201_1", "프로젝트 정보가 등록되었습니다."),
    PROJECT_LIST_FOUND(HttpStatus.OK, "PROJECT200_1", "프로젝트 목록을 조회했습니다."),
    PROJECT_UPDATED(HttpStatus.OK, "PROJECT200_2", "프로젝트 정보가 수정되었습니다."),
    PROJECT_DELETED(HttpStatus.OK, "PROJECT200_3", "프로젝트 정보가 삭제되었습니다."),

    BOOKMARK_CREATED(HttpStatus.CREATED, "BOOKMARK201_1", "포트폴리오를 북마크했습니다."),
    BOOKMARK_DELETED(HttpStatus.OK, "BOOKMARK200_1", "북마크를 취소했습니다."),

    TEMPLATE_LIST_FOUND(HttpStatus.OK, "TEMPLATE200_1", "템플릿 목록을 조회했습니다."),
    TEMPLATE_DETAIL_FOUND(HttpStatus.OK, "TEMPLATE200_2", "템플릿 상세를 조회했습니다."),

    PORTFOLIO_FIELD_LIST_FOUND(HttpStatus.OK, "FIELD200_1", "포트폴리오 필드 목록을 조회했습니다."),
    PORTFOLIO_FIELD_UPDATED(HttpStatus.OK, "FIELD200_2", "포트폴리오 필드 내용이 수정되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
