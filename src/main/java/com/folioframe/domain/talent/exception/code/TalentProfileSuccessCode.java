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

    // 200 OK
    PROFILE_READ_SUCCESS(HttpStatus.OK, "TALENT200_1", "내 프로필 조회가 완료되었습니다."),
    PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "TALENT200_2", "인재 프로필 수정이 완료되었습니다."),
    PROFILE_SEARCH_SUCCESS(HttpStatus.OK, "TALENT200_3", "인재 검색 결과입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}