package com.folioframe.domain.activity.controller;

import com.folioframe.domain.activity.dto.response.ActivityResDTO;
import com.folioframe.domain.activity.enums.ActivityCategory;
import com.folioframe.domain.activity.enums.ActivitySortType;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Activity", description = "대외활동(공모전/해커톤) 목록 조회 API")
public interface ActivityControllerDocs {

    @Operation(
            summary = "대외활동 목록 조회",
            description = "대외활동 목록을 페이지 단위로 조회합니다. (3×3, 기본 9개/페이지)\n\n" +
                    "- `category`: CONTEST(공모전) / HACKATHON(해커톤) / 미입력 시 전체\n" +
                    "- `sort`: LATEST(최신순, 기본값) / POPULAR(북마크 순) / MOST_VIEWED(조회순)\n" +
                    "- `X-Member-Id` 헤더 전달 시 각 항목의 `bookmarked` 여부를 함께 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "목록 조회 성공")
    })
    ResponseEntity<ApiResponse<PageResponse<ActivityResDTO>>> getActivities(
            @Parameter(description = "카테고리 (CONTEST / HACKATHON / 미입력 시 전체)")
            @RequestParam(required = false) ActivityCategory category,
            @Parameter(description = "정렬 (LATEST / POPULAR / MOST_VIEWED, 기본값: LATEST)")
            @RequestParam(required = false) ActivitySortType sort,
            @Parameter(description = "페이지 번호 (1부터 시작, 기본값: 1)")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 9)")
            @RequestParam(defaultValue = "9") Integer size,
            @Parameter(description = "인증된 회원 ID (선택 — 전달 시 북마크 여부 포함)")
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId
    );

    @Operation(
            summary = "대외활동 조회수 증가",
            description = "대외활동 원문 링크 클릭 시 호출합니다. 해당 활동의 조회수를 1 증가시킵니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회수 반영 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대외활동을 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<Void>> incrementViewCount(
            @Parameter(description = "대외활동 ID", required = true) @PathVariable Long activityId
    );

    @Operation(
            summary = "북마크한 대외활동 목록 조회",
            description = "로그인한 회원이 북마크한 대외활동 목록을 페이지 단위로 조회합니다. (기본 9개/페이지)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다.")
    })
    ResponseEntity<ApiResponse<PageResponse<ActivityResDTO>>> getBookmarkedActivities(
            @Parameter(description = "페이지 번호 (1부터 시작, 기본값: 1)")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 9)")
            @RequestParam(defaultValue = "9") Integer size,
            @Parameter(description = "인증된 회원 ID", required = true)
            @RequestHeader("X-Member-Id") Long memberId
    );
}
