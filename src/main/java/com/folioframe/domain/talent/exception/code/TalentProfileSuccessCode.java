package com.folioframe.domain.talent.exception.code;

import com.folioframe.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TalentProfileSuccessCode implements BaseSuccessCode {

    // 201 Created
    PROFILE_CREATED(HttpStatus.CREATED, "TALENT201_1", "인재 프로필이 성공적으로 등록되었습니다."),
    CAREER_CREATED(HttpStatus.CREATED, "TALENT201_2", "경력 정보가 등록되었습니다."),
    EDUCATION_CREATED(HttpStatus.CREATED, "TALENT201_3", "학력 정보가 등록되었습니다."),
    CERTIFICATE_CREATED(HttpStatus.CREATED, "TALENT201_4", "자격증 정보가 등록되었습니다."),

    // 200 OK
    PROFILE_READ_SUCCESS(HttpStatus.OK, "TALENT200_1", "내 프로필 조회가 완료되었습니다."),
    PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "TALENT200_2", "인재 프로필 수정이 완료되었습니다."),
    CAREER_LIST_FOUND(HttpStatus.OK, "TALENT200_4", "경력 목록을 조회했습니다."),
    CAREER_UPDATED(HttpStatus.OK, "TALENT200_5", "경력 정보가 수정되었습니다."),
    CAREER_DELETED(HttpStatus.OK, "TALENT200_6", "경력 정보가 삭제되었습니다."),
    EDUCATION_LIST_FOUND(HttpStatus.OK, "TALENT200_7", "학력 목록을 조회했습니다."),
    EDUCATION_UPDATED(HttpStatus.OK, "TALENT200_8", "학력 정보가 수정되었습니다."),
    EDUCATION_DELETED(HttpStatus.OK, "TALENT200_9", "학력 정보가 삭제되었습니다."),
    CERTIFICATE_LIST_FOUND(HttpStatus.OK, "TALENT200_10", "자격증 목록을 조회했습니다."),
    CERTIFICATE_UPDATED(HttpStatus.OK, "TALENT200_11", "자격증 정보가 수정되었습니다."),
    CERTIFICATE_DELETED(HttpStatus.OK, "TALENT200_12", "자격증 정보가 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}