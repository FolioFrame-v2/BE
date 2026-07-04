package com.folioframe.domain.common.controller;

import com.folioframe.domain.common.dto.response.RegionResDTO;
import com.folioframe.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Region", description = "지역 목록 조회 API")
public interface RegionControllerDocs {

    @Operation(
            summary = "지역 목록 조회 (시/도 → 시/군/구 드롭다운)",
            description = """
                    시/도 → 시/군/구 2단계 드롭다운 UI에 사용합니다. 대외활동/공고 지역 필터, 프로필 지역 등록 모두 동일하게 이 API를 씁니다.
                    - `parentId` 미입력: 최상위 시/도 목록(서울/경기/인천/부산/대구/광주/원격)을 반환합니다. 1단계 드롭다운에 사용합니다.
                    - `parentId={시/도 ID}`: 그 시/도 하위의 시/군/구 목록(전체 포함)을 반환합니다. 2단계 드롭다운에 사용합니다.
                    새 지역을 등록하는 기능은 없습니다 — 관리자가 미리 정의한 목록 안에서만 선택 가능합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<List<RegionResDTO>>> getRegions(
            @Parameter(description = "이 시/도 ID 하위의 시/군/구 목록 반환. 미입력 시 최상위 시/도 목록 반환") @RequestParam(required = false) Long parentId
    );
}
