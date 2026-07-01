package com.folioframe.domain.activity.controller;

import com.folioframe.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Activity Bookmark", description = "대외활동 북마크 등록·취소 API")
public interface ActivityBookmarkControllerDocs {

    @Operation(
            summary = "대외활동 북마크",
            description = "대외활동을 북마크합니다. 이미 북마크된 대외활동은 409 에러를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "북마크 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대외활동 또는 회원을 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 북마크된 대외활동입니다.")
    })
    ResponseEntity<ApiResponse<Void>> bookmark(
            @Parameter(description = "대외활동 ID", required = true) @PathVariable Long activityId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );

    @Operation(
            summary = "대외활동 북마크 취소",
            description = "북마크된 대외활동의 북마크를 취소합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "북마크 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대외활동, 회원, 또는 북마크를 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<Void>> cancelBookmark(
            @Parameter(description = "대외활동 ID", required = true) @PathVariable Long activityId,
            @Parameter(description = "인증된 회원 ID", required = true) @RequestHeader("X-Member-Id") Long memberId
    );
}
