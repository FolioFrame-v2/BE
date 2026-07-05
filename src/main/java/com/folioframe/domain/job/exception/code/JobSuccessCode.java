package com.folioframe.domain.job.exception.code;

import com.folioframe.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JobSuccessCode implements BaseSuccessCode {

    // --- Posting 관련 성공 ---
    JOB_POSTING_LIST_FETCHED(HttpStatus.OK, "JOB200_1", "채용 공고 목록 조회가 완료되었습니다."),
    JOB_POSTING_FETCHED(HttpStatus.OK, "JOB200_2", "채용 공고 상세 조회가 완료되었습니다."),
    JOB_POSTING_UPDATED(HttpStatus.OK, "JOB200_3", "채용 공고가 성공적으로 수정되었습니다."),
    JOB_POSTING_BOOKMARK_TOGGLED(HttpStatus.OK, "JOB200_4", "채용 공고 북마크 상태가 변경되었습니다."),
    JOB_POSTING_CREATED(HttpStatus.CREATED, "JOB201_1", "채용 공고가 성공적으로 등록되었습니다."),
    JOB_POSTING_DELETED(HttpStatus.NO_CONTENT, "JOB204_1", "채용 공고가 성공적으로 삭제되었습니다."),

    // --- Application 관련 성공 ---
    APPLICATION_LIST_FETCHED(HttpStatus.OK, "APP200_1", "지원 내역 목록 조회가 완료되었습니다."),
    APPLICATION_STATUS_UPDATED(HttpStatus.OK, "APP200_2", "지원 상태가 성공적으로 변경되었습니다."),
    APPLICATION_CREATED(HttpStatus.CREATED, "APP201_1", "성공적으로 지원되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}