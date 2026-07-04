package com.folioframe.domain.activity.controller;

import com.folioframe.domain.activity.dto.response.ActivityResDTO;
import com.folioframe.domain.activity.enums.ActivityCategory;
import com.folioframe.domain.activity.enums.ActivityField;
import com.folioframe.domain.activity.enums.ActivitySortType;
import com.folioframe.domain.activity.enums.ActivityTeamSize;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Activity", description = "대외활동(공모전/해커톤) 목록 조회 API")
public interface ActivityControllerDocs {

    @Operation(
            summary = "대외활동 목록 조회",
            description = "대외활동 목록을 페이지 단위로 조회합니다. (3×3, 기본 9개/페이지)\n\n" +
                    "- `keyword`: 공모전명/주최사에 대한 검색어 (부분 일치, 미입력 시 전체)\n" +
                    "- `category`: 유형 — CONTEST(공모전) / HACKATHON(해커톤) / 미입력 시 전체. 하나만 선택 가능\n" +
                    "- `regionId`: 지역 (GET /api/v1/regions 로 조회한 region의 ID). 시/도(최상위) ID를 주면 그 하위 시/군/구 전체가 포함되고, 시/군/구(리프) ID를 주면 그 지역만 정확히 조회됩니다. 미입력 시 전체. 하나만 선택 가능\n" +
                    "- `field`: 분야 — AI_ML(AI/ML) / HEALTHCARE(헬스케어) / EDUTECH(에듀테크) / LIFESTYLE(라이프스타일) / OPEN_SOURCE(오픈소스) / INFRA(인프라) / ETC(기타) / 미입력 시 전체. 하나만 선택 가능\n" +
                    "- `teamSize`: 인원수 — ONE(1인) / TWO_TO_THREE(2~3인) / FOUR_TO_SIX(4~6인) / SEVEN_PLUS(7인+) / 미입력 시 전체. 하나만 선택 가능\n" +
                    "- `sort`: LATEST(최신순, 기본값) / POPULAR(북마크 순) / MOST_VIEWED(조회순)\n" +
                    "- 비로그인 사용자도 조회 가능합니다. 로그인 상태면 각 항목의 `bookmarked` 여부를 함께 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "목록 조회 성공")
    })
    ResponseEntity<ApiResponse<PageResponse<ActivityResDTO>>> getActivities(
            @Parameter(description = "검색어 (공모전명/주최사, 부분 일치)")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "유형 (CONTEST / HACKATHON / 미입력 시 전체)")
            @RequestParam(required = false) ActivityCategory category,
            @Parameter(description = "지역 ID (region.region_id). 시/도 ID면 하위 시/군/구 전체 포함, 시/군/구 ID면 정확히 일치하는 것만 조회. 미입력 시 전체")
            @RequestParam(required = false) Long regionId,
            @Parameter(description = "분야 (AI_ML / HEALTHCARE / EDUTECH / LIFESTYLE / OPEN_SOURCE / INFRA / ETC / 미입력 시 전체)")
            @RequestParam(required = false) ActivityField field,
            @Parameter(description = "인원수 (ONE / TWO_TO_THREE / FOUR_TO_SIX / SEVEN_PLUS / 미입력 시 전체)")
            @RequestParam(required = false) ActivityTeamSize teamSize,
            @Parameter(description = "정렬 (LATEST / POPULAR / MOST_VIEWED, 기본값: LATEST)")
            @RequestParam(required = false) ActivitySortType sort,
            @Parameter(description = "페이지 번호 (1부터 시작, 기본값: 1)")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 9)")
            @RequestParam(defaultValue = "9") Integer size,
            @Parameter(hidden = true) Long memberId
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
            @Parameter(hidden = true) Long memberId
    );
}
